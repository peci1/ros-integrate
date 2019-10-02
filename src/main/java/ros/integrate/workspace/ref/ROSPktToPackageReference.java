package ros.integrate.workspace.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktTypeBase;
import ros.integrate.workspace.psi.ROSPackage;

/**
 * a class defining the references of {@link ROSPktTypeBase} to {@link ROSPackage} and its affiliated roots.
 */
public class ROSPktToPackageReference extends ROSPackageReferenceBase<ROSPktTypeBase> {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    ROSPktToPackageReference(@NotNull ROSPktTypeBase element, @NotNull TextRange textRange) {
        super(element, textRange);
        pkgName = element.raw().getText().replaceAll("/.*","");
        assert pkgName.length() == textRange.getLength();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        String pkt = myElement.getText().replaceAll(".*/","");
        return super.handleElementRename((newElementName.equals(pkgName) ? "" : (newElementName + "/")) + pkt);
    }
}