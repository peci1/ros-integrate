// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ros.integrate.pkt.psi.ROSPktTypes.*;
import ros.integrate.pkt.psi.*;

public class ROSPktTypeFragImpl extends ROSPktTypeBaseImpl implements ROSPktTypeFrag {

  public ROSPktTypeFragImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ROSPktVisitor visitor) {
    visitor.visitTypeFrag(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ROSPktVisitor) accept((ROSPktVisitor)visitor);
    else super.accept(visitor);
  }

}
