package dfa.analyses;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import dfa.analyses.ConstantBitsElement.BitValue;
import dfa.analyses.ConstantBitsElement.BitValueArray;
import dfa.framework.Join;
import soot.jimple.internal.JimpleLocal;

/**
 * @author Nils Jessen
 * 
 *         A {@code ConstantBitsJoin} performs the join for a {@code ConstantBitsAnalysis}.
 */
public class ConstantBitsJoin implements Join<ConstantBitsElement> {

    @Override
    public ConstantBitsElement join(Set<ConstantBitsElement> elements) {
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("elements must not be empty");
        }

        Iterator<ConstantBitsElement> it = elements.iterator();
        ConstantBitsElement refElement = it.next();
        Map<JimpleLocal, BitValueArray> refMap = refElement.getLocalMap();
        while (it.hasNext()) {
            ConstantBitsElement compElement = it.next();
            Map<JimpleLocal, BitValueArray> compMap = compElement.getLocalMap();
            for (Map.Entry<JimpleLocal, BitValueArray> entry : refMap.entrySet()) {
                if (!compMap.containsKey(entry.getKey())) {
                    throw new IllegalArgumentException("locals not matching");
                }
            }

            for (Map.Entry<JimpleLocal, BitValueArray> entry : compMap.entrySet()) {
                if (!refMap.containsKey(entry.getKey())) {
                    throw new IllegalArgumentException("locals not matching");
                }
            }
        }

        ConstantBitsElement result = new ConstantBitsElement();
        for (Map.Entry<JimpleLocal, BitValueArray> entry : refMap.entrySet()) {
            Iterator<ConstantBitsElement> elementIt = elements.iterator();
            JimpleLocal local = entry.getKey();
            
            if (!elementIt.hasNext()) {
                assert false : "elements must not be empty is checked above!";
            }
            
            BitValueArray tmp = elementIt.next().getValue(local);
            int length = tmp.getLength();
            BitValueArray top;
            BitValueArray bottom;
            if (length == BitValueArray.INT_SIZE) {
                top = BitValueArray.getIntTop();
                bottom = BitValueArray.getIntBottom();
            } else if (length == BitValueArray.LONG_SIZE) {
                top = BitValueArray.getLongTop();
                bottom = BitValueArray.getLongBottom();
            } else {
                throw new IllegalStateException("BitValueArrays must be of size INT_SIZE or LONG_SIZE");
            }

            boolean broke = false;
            while (elementIt.hasNext()) {
                ConstantBitsElement current = elementIt.next();
                BitValueArray currentVal = current.getValue(local);

                if (currentVal.getLength() != length) {
                    // First check if BitValueArrays to be joined are of same size
                    throw new IllegalStateException("Unable to join BitValueArrays of different size!");
                }

                if (tmp.equals(top) || currentVal.equals(top)) {
                    // Second if one is TOP, if so, no need to check further
                    result.setValue(local, top);
                    broke = true;
                    break;

                } else if (tmp.equals(bottom)) {
                    // Third if tmp is bottom, new tmp is currentVal since join(bottom, x) = x for all x
                    tmp = currentVal;

                    // Fourth if currentVal is bottom, tmp remains untouched since join(bottom, x) = x for all x
                    // Also if tmp equals currentVal, tmp remains untouched, since nothing would change
                    // But if tmp does not equal currentVal and currentVal is not bottom, we have to compare them bit by bit
                } else if (!tmp.equals(currentVal) && !currentVal.equals(bottom)) {
                    BitValue[] bitValues = new BitValue[length];
                    for (int i = 0; i < length; i++) {
                        BitValue currentValBit = currentVal.getBitValues()[i];
                        BitValue tmpBit = tmp.getBitValues()[i];
                        
                        if (currentValBit.equals(BitValue.TOP) || tmpBit.equals(BitValue.TOP)) {
                            // If one of the two bits is TOP, the resulting bit is TOP
                            bitValues[i] = BitValue.TOP;
                            
                        } else if (currentValBit.equals(BitValue.BOTTOM)) {
                            // If one of the bits is BOTTOM, the resulting bit is whatever the other bit was
                            bitValues[i] = tmpBit;
                        } else if (tmp.getBitValues()[i].equals(BitValue.BOTTOM)) {
                            // If one of the bits is BOTTOM, the resulting bit is whatever the other bit was
                            bitValues[i] = currentValBit;
                            
                        } else if (currentValBit.equals(tmpBit)) {
                            // If they are the same, we just take one of them
                            bitValues[i] = currentValBit;
                            
                        } else {
                            // If they are both neither bottom nor the same, the resulting bit is TOP
                            bitValues[i] = BitValue.TOP;
                        }
                    }
                    tmp = new BitValueArray(bitValues);
                }

            }
            if (!broke) {
                result.setValue(local, tmp);
            }
        }

        return result;
    }
    
}
