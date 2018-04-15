package nz.ac.auckland.cs;

import soot.*;
import soot.jimple.*;

import java.util.Iterator;
import java.util.Map;

public class DebugTransform extends BodyTransformer {
    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        final PatchingChain<Unit> units = b.getUnits();
        for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
            final Unit u = iter.next();
            u.apply(new AbstractStmtSwitch() {
                @Override
                public void caseInvokeStmt(InvokeStmt stmt) {
                    InvokeExpr invokeExpr = stmt.getInvokeExpr();
                    if (invokeExpr.getMethod().getName().equals("onCreate")) {
                        SootMethod toCall = Scene.v().getSootClass("android.util.Log").getMethod("int i(java.lang.String,java.lang.String)");
                        units.insertBefore(Jimple.v().newInvokeStmt(
                                Jimple.v().newStaticInvokeExpr(toCall.makeRef(), StringConstant.v("test"), StringConstant.v("obfuscateddebug"))
                        ), u);
                        b.validate();
                    }
                }
            });
        }
    }
}
