package ros.integrate.pkg.xml.condition.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.intention.CleanItemQuickFix;
import ros.integrate.pkg.xml.condition.intention.PrependLogicQuickFix;
import ros.integrate.pkg.xml.condition.psi.ROSConditionExpr;
import ros.integrate.pkg.xml.condition.psi.ROSConditionItem;
import ros.integrate.pkg.xml.condition.psi.ROSConditionToken;

import java.util.List;

public class ROSConditionAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof ROSConditionExpr) {
            ROSConditionExpr expr = (ROSConditionExpr) element;
            int exprSequence = 0;
            List<ROSConditionToken> tokens = expr.getTokens();
            for (ROSConditionToken token : tokens) {
                if (token instanceof ROSConditionExpr) {
                    // the parser makes sure the conditions are only recognised as such in valid places.
                    exprSequence++;
                } else {
                    exprSequence = 0;
                }

                if (exprSequence > 1) {
                    Annotation ann = holder.createErrorAnnotation(token,
                            "Expressions must be separated by logic operators or comparisons.");
                    ann.registerFix(new PrependLogicQuickFix(expr, token));
                }
            }
        }
        if (element instanceof ROSConditionItem) {
            String text = element.getText();
            if (text.startsWith("$")) {
                if (!text.substring(1).replaceAll("[a-zA-Z0-9_]","").isEmpty()) {
                    Annotation ann = holder.createErrorAnnotation(element,
                            "Variables may only contain alphanumerics and underscores.");
                    ann.registerFix(new CleanItemQuickFix(element));
                }
            } else {
                if (!text.replaceAll("[-a-zA-Z0-9_]","").isEmpty()) {
                    Annotation ann = holder.createErrorAnnotation(element,
                            "Literals may only contain alphanumerics, underscores, and dashes.");
                    ann.registerFix(new CleanItemQuickFix(element));
                }
            }
        }
    }
}
