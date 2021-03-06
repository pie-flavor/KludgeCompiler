package flavor.pie.kludgec

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class KludgeClassBuilder(private val delegateBuilder: ClassBuilder) : DelegatingClassBuilder() {

    companion object {
        const val timingsAnnotation = "flavor.pie.kludge.Timed"
        const val timingsVar = 8843
        const val exceptionVar = 8842
    }

    override fun getDelegate(): ClassBuilder = delegateBuilder

    override fun newMethod(
        origin: JvmDeclarationOrigin,
        access: Int,
        name: String,
        desc: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val original = super.newMethod(origin, access, name, desc, signature, exceptions)
        val function = origin.descriptor as? FunctionDescriptor ?: return original
        val annotation = function.annotations.findAnnotation(FqName(timingsAnnotation)) ?: return original
        val timingName = annotation.allValueArguments.getValue(Name.identifier("value")).value as? String ?: function.name.identifier
        val lineNo = origin.element?.containingFile?.text?.substring(0..origin.element!!.textRange.startOffset)?.count { it == '\n' } // why is there no 'get line number' method ffs
        return object: MethodVisitor(Opcodes.ASM5, original) {

            val tryStart = Label()
            val tryEnd = Label()
            val jumpTo = Label()

            override fun visitCode() {
                super.visitCode()
                InstructionAdapter(this).apply {
                    val startLabel = Label()
                    visitLabel(startLabel)
                    if (lineNo != null) {
                        visitLineNumber(lineNo, startLabel)
                    }
                    invokestatic("flavor/pie/kludge/GlobalKt", "getPlugin", "()Ljava/lang/Object;", false) // plugin
                    visitLdcInsn(timingName) // plugin | name constant
                    invokestatic("co/aikar/timings/Timings", "ofStart", "(Ljava/lang/Object;Ljava/lang/String;)Lco/aikar/timings/Timing;", false) // timing
                    store(timingsVar, Type.getType("Lco/aikar/timings/Timings;")) // _
                    visitTryCatchBlock(tryStart, tryEnd, jumpTo, null)
                    visitLabel(tryStart)
                    nop()
                }
            }

            override fun visitInsn(opcode: Int) {
                when (opcode) {
                    Opcodes.RETURN, Opcodes.ARETURN, Opcodes.IRETURN, Opcodes.DRETURN, Opcodes.FRETURN,
                    Opcodes.LRETURN -> {
                        InstructionAdapter(this).apply {
                            val endLabel = Label()
                            visitLabel(endLabel)
                            if (lineNo != null) {
                                visitLineNumber(lineNo, endLabel)
                            }
                            visitVarInsn(Opcodes.ALOAD, timingsVar) // timings
                            aconst(null) // timings | null
                            invokestatic("kotlin/jdk7/AutoCloseableKt", "closeFinally", "(Ljava/lang/AutoCloseable;Ljava/lang/Throwable;)V", false) // _
                            visitLabel(tryEnd)
                            nop()
                        }
                        super.visitInsn(opcode)
                    }
                    else -> super.visitInsn(opcode)
                }
            }

            override fun visitMaxs(maxStack: Int, maxLocals: Int) {
                InstructionAdapter(this).apply {
                    visitLabel(jumpTo) // exception
                    if (lineNo != null) {
                        visitLineNumber(lineNo, jumpTo)
                    }
                    dup() // exception | exception
                    visitVarInsn(Opcodes.ALOAD, timingsVar) // exception | exception | timings
                    swap() // exception | timings | exception
                    invokestatic(
                        "kotlin/jdk7/AutoCloseableKt",
                        "closeFinally",
                        "(Ljava/lang/AutoCloseable;Ljava/lang/Throwable;)V",
                        false
                    ) // exception
                    athrow()
                }
                super.visitMaxs(maxStack, maxLocals)
            }
        }
    }

}
