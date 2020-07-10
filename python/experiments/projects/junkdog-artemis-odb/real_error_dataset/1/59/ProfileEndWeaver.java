package com.artemis.weaver.profile;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import com.artemis.meta.ClassMetadata;

class ProfileEndWeaver extends AdviceAdapter implements Opcodes {
	private ClassMetadata info;
	
	ProfileEndWeaver(MethodVisitor methodVisitor, ClassMetadata info, int access, String name, String desc) {
		super(ASM4, methodVisitor, access, name, desc);
		this.info = info;
	}

	@Override
	protected void onMethodExit(int opcode) {
		String systemName = info.type.getInternalName();
		String profiler = info.profilerClass.getInternalName();
		String profileDescriptor = info.profilerClass.getDescriptor();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, systemName, "$profiler", profileDescriptor);
		mv.visitMethodInsn(INVOKEVIRTUAL, profiler, "stop", "()V", false);
	}
}
