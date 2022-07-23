package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class BlockBorderClassVisitor extends BlockNpcRedstoneClassVisitor {
    public BlockBorderClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    protected MethodVisitor activatedVisitor(int api, MethodVisitor mv) {
        return new BlockNpcRedstoneClassVisitor.WorldIsRemoteVisitor(api, mv, false);
    }
}
