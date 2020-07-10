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
        if (elements == null) {
            throw new IllegalArgumentException("elements must not be null");
        }

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
            BitValueArray tmp = elementIt.next().getBitValues(local);
            int l = tmp.getLength();
            BitValueArray top;
            BitValueArray bottom;
            if (l == BitValueArray.INT_SIZE) {
                top = BitValueArray.getIntTop();
                bottom = BitValueArray.getIntBottom();
            } else if (l == BitValueArray.LONG_SIZE) {
                top = BitValueArray.getLongTop();
                bottom = BitValueArray.getLongBottom();
            } else {
                throw new IllegalStateException("BitValueArrays must be of size INT_SIZE or LONG_SIZE");
            }

            boolean broke = false;
            while (elementIt.hasNext()) {
                ConstantBitsElement current = elementIt.next();
                BitValueArray currentVal = current.getBitValues(local);

                // First check if BitValueArrays to be joined are of same size
                if (currentVal.getLength() != l) {
                    throw new IllegalStateException("Unable to join BitValueArrays of different size!");
                }

                // Second if one is TOP, if so, no need to check further
                if (tmp.equals(top) || currentVal.equals(top)) {
                    result.setBitValues(local, top);
                    broke = true;
                    break;

                    // Third if tmp is bottom, new tmp is currentVal since join(bottom, x) = x for all x
                } else if (tmp.equals(bottom)) {
                    tmp = currentVal;

                    // Fourth if currentVal is bottom, tmp remains untouched since join(bottom, x) = x for all x
                    // Also if tmp equals currentVal, tmp remains untouched, since nothing would change
                    // But if tmp does not equal currentVal and currentVal is not bottom, we have to compare them bit by bit
                } else if (!tmp.equals(currentVal) && !currentVal.equals(bottom)) {
                    BitValue[] bitValues = new BitValue[l];
                    for (int i = 0; i < l; i++) {
                        BitValue currentValBit = currentVal.getBitValues()[i];
                        BitValue tmpBit = tmp.getBitValues()[i];
                        
                        // If one of the two bits is TOP, the resulting bit is TOP
                        if (currentValBit.equals(BitValue.TOP) || tmpBit.equals(BitValue.TOP)) {
                            bitValues[i] = BitValue.TOP;
                            
                        // If one of the bits is BOTTOM, the resulting bit is whatever the other bit was
                        } else if (currentValBit.equals(BitValue.BOTTOM)) {
                            bitValues[i] = tmpBit;
                        } else if (tmp.getBitValues()[i].equals(BitValue.BOTTOM)) {
                            bitValues[i] = currentValBit;
                            
                        // If they are the same, we just take one of them
                        } else if (currentValBit.equals(tmpBit)) {
                            bitValues[i] = currentValBit;
                            
                        // If they are both neither bottom nor the same, the resulting bit is TOP
                        } else {
                            bitValues[i] = BitValue.TOP;
                        }
                    }
                    tmp = new BitValueArray(bitValues);
                }

            }
            if (!broke) {
                result.setBitValues(local, tmp);
            }
        }

        return result;
    }

    // Old Version, where BitValueArrays of different lengths can be joined:
    // ConstantBitsElement result = new ConstantBitsElement();
    // for (Map.Entry<JimpleLocal, BitValueArray> entry : refMap.entrySet()) {
    // Iterator<ConstantBitsElement> elementIt = elements.iterator();
    // JimpleLocal local = entry.getKey();
    // BitValueArray tmp = BitValueArray.getIntBottom();
    //
    // while (elementIt.hasNext()) {
    // ConstantBitsElement current = elementIt.next();
    // BitValueArray currentVal = current.getBitValues(local);
    //
    // if (currentVal.equals(BitValueArray.getIntTop()) || currentVal.equals(BitValueArray.getLongTop())) {
    // result.setBitValues(local, currentVal);
    // break;
    // } else if (tmp.equals(BitValueArray.getIntBottom())) {
    // tmp = currentVal;
    // } else if (!tmp.equals(currentVal)) {
    // int m = Math.max(tmp.getLength(), currentVal.getLength());
    // BitValue[] bitValues = new BitValue[m];
    // for (int i = 0; i < m; i++) {
    // if (i < currentVal.getLength()) {
    // BitValue currentValBit = currentVal.getBitValues()[i];
    // if (i < tmp.getLength()) {
    // BitValue tmpBit = tmp.getBitValues()[i];
    // if (currentValBit.equals(BitValue.TOP) || tmpBit.equals(BitValue.TOP)) {
    // bitValues[i] = BitValue.TOP;
    // } else if (currentValBit.equals(BitValue.BOTTOM)) {
    // bitValues[i] = tmpBit;
    // } else if (tmp.getBitValues()[i].equals(BitValue.BOTTOM)) {
    // bitValues[i] = currentValBit;
    // } else if (currentValBit.equals(tmpBit)) {
    // bitValues[i] = currentValBit;
    // } else {
    // bitValues[i] = BitValue.TOP;
    // }
    // } else {
    // bitValues[i] = currentValBit;
    // }
    // } else if (i < tmp.getLength()) {
    // bitValues[i] = tmp.getBitValues()[i];
    // } else {
    // throw new IllegalStateException("How did I get here?");
    // }
    // }
    // tmp = new BitValueArray(bitValues);
    // }
    //
    // }
    // if (!(result.getBitValues(local).equals(BitValueArray.getIntTop())
    // || result.getBitValues(local).equals(BitValueArray.getLongTop()))) {
    // result.setBitValues(local, tmp);
    // }
    // }
    //
    // return result;
    // }

}
