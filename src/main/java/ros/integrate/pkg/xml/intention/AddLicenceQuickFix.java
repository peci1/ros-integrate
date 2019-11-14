package ros.integrate.pkg.xml.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;

public class AddLicenceQuickFix extends BaseIntentionAction {
    private ROSPackageXml pkgXml;

    public AddLicenceQuickFix(ROSPackageXml pkgXml) {
        this.pkgXml = pkgXml;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @NotNull
    @Override
    public String getText() {
        return "Add licence";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return pkgXml.getLicences().isEmpty();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, PsiFile file) throws IncorrectOperationException {
        pkgXml.addLicence("TODO");
        TextRange range = pkgXml.getLicenceTextRanges().get(0);
        Caret caret = editor.getCaretModel().getCurrentCaret();
        caret.moveToOffset(range.getStartOffset());
        caret.moveCaretRelatively("TODO".length(), 0, true, false);

    }
}
