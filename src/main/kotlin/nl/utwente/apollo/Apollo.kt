package nl.utwente.apollo

import nl.utwente.apollo.pmd.ApolloPMDRunner
import nl.utwente.apollo.pmd.drawing.DrawingReportRule
import nl.utwente.apollo.pmd.loops.LoopReportRule
import nl.utwente.apollo.pmd.messagepassing.MessagePassingReportRule
import nl.utwente.apollo.pmd.oo.OoReportRule
import nl.utwente.apollo.pmd.physics.PhysicsReportRule
import nl.utwente.processing.ProcessingFile
import nl.utwente.processing.ProcessingProject
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BiPredicate
import java.util.stream.Collectors
import kotlin.math.roundToInt

object Apollo {
    @JvmStatic fun verbalize(probability: Double, includeNumeral: Boolean = false): String {
        val word = when {
            probability <= 0.20  -> "improbable"
            probability <= 0.375 -> "uncertain"
            probability <= 0.675 -> "fifty-fifty"
            probability <= 0.80  -> "expected"
            probability <= 0.95  -> "probable"
            else                 -> "almost certain"
        }

        return if (includeNumeral)
            word + " (" + (probability * 100.0).roundToInt() + "%)"
        else
            word
    }

    @JvmStatic fun formatResults(metrics: Map<Metrics, Double>, includeNumeral: Boolean = false): String {
        val s = StringBuilder()
        s.appendln("Based on this submission, Apollo thinks it is")

        s.append("- ")
        s.append(verbalize(DrawingReportRule.calculateFinal(metrics), includeNumeral))
        s.appendln(" that the student can write a program that uses graphical commands to draw to the screen")

        s.append("- ")
        s.append(verbalize(LoopReportRule.calculateFinal(metrics), includeNumeral))
        s.appendln(" that the student can write a program that uses looping constructs for repetition")

        s.append("- ")
        s.append(verbalize(OoReportRule.calculateFinal(metrics), includeNumeral))
        s.appendln(" that the student can compose a program using classes, objects and methods to structure the code in an object-oriented way")

        s.append("- ")
        s.append(verbalize(MessagePassingReportRule.calculateFinal(metrics), includeNumeral))
        s.appendln(" that the student can implement message passing to enable communication between classes in a complex program")

        s.append("- ")
        s.append(verbalize(PhysicsReportRule.calculateFinal(metrics), includeNumeral))
        s.appendln(" that the student can use elementary vector operations to simulate physical forces")

        return s.toString()
    }

    @JvmStatic fun main(args: Array<String>) {
        if (args.size < 1) {
            println("Usage: <project path>")
            return
        }

        val path = Path.of(args[0])
        val project = ProcessingProject(
            Files.find(path, 6, BiPredicate { p, attr -> attr.isRegularFile && p.fileName.toString().endsWith(".pde") })
                    .map {p -> ProcessingFile(p.fileName.toString(), p.fileName.toString(), Files.readString(p)) }
                    .collect(Collectors.toList()))

        val runner = ApolloPMDRunner()
        val metrics = runner.run(project)

        // Reporting
        println("Metrics:")
        for ((metric, value) in metrics) {
            println(" - $metric = $value")
        }

        println("\nConclusions:")
        print(Apollo.formatResults(metrics, true))
    }
}
