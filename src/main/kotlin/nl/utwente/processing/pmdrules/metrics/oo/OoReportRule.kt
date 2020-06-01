package nl.utwente.processing.pmdrules.metrics.oo

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule
import nl.utwente.processing.pmdrules.metrics.Metrics
import nl.utwente.processing.pmdrules.metrics.weightedAverage

class OoReportRule : AbstractJavaRule() {
    companion object {
        fun calculateFinal(metrics: Map<Metrics, Double>) : Double {
            return weightedAverage(
                    Pair(1.0, metrics[Metrics.OO_CLASSCOUNT]),
                    Pair(1.0, metrics[Metrics.OO_SMELLS])
            )
        }
    }

    override fun visit(node: ASTCompilationUnit, data: Any?): Any? {
        val metric = OoMetrics.ClassCountMetric()
        val classCount = metric.computeFor(node, null)
        this.addViolationWithMessage(data, node, message, arrayOf(Metrics.OO_RAW_CLASSCOUNT, classCount))
        this.addViolationWithMessage(data, node, message, arrayOf(Metrics.OO_CLASSCOUNT, metric.computeProbability(classCount)))

        return super.visit(node, data)
    }
}