package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.RemoveDependencyQuickFix;

import java.util.*;
import java.util.stream.Collectors;

class PackageDependencyAnnotator {
    @NotNull
    private final ROSPackageXml pkgXml;
    @NotNull
    private final AnnotationHolder holder;

    @NotNull
    private final List<Pair<DependencyType, ROSPackage>> dependencies;

    @NotNull
    private final List<Pair<TextRange,TextRange>> depTrs;

    @Contract(pure = true)
    PackageDependencyAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.holder = holder;
        this.pkgXml = pkgXml;
        dependencies = pkgXml.getDependenciesTyped();
        depTrs = pkgXml.getDependencyTextRanges();
    }

    void annSelfDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (pkgXml.getPackage().equals(dependencies.get(i).second)) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).second,
                        "A package cannot depend on itself.");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
            }
        }
    }

    void invalidDependencyName() {
        // get invalid tag names
        List<String> relevant = Arrays.stream(DependencyType.values())
                .filter(dep -> !dep.relevant(pkgXml.getFormat())).map(DependencyType::getTagName)
                .collect(Collectors.toList());
        for (int i = 0; i < depTrs.size(); i++) {
            String tagName = dependencies.get(i).first.getTagName();
            if (relevant.contains(tagName)) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).first,
                        "Dependency tag " + tagName + " may not be used in manifest format " +
                                pkgXml.getFormat() + ".");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
//                ann.registerFix(new ReformatPackageXmlFix(pkgXml));
            }
        }
    }

    void annEmptyDependency() {
        for (int i = 0; i < dependencies.size(); i++) {
            if (depTrs.get(i).second.getLength() == 0) {
                Annotation ann = holder.createErrorAnnotation(depTrs.get(i).first,
                        "Empty dependency tag.");
                ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
            }
        }
    }

    void annConflictingDependencies() {
        Set<Integer> trsToAnn = new HashSet<>();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            Pair<DependencyType, ROSPackage> di = dependencies.get(i), dj;
            boolean found = false;
            for (int j = i - 1; j >= 0; j--) {
                dj = dependencies.get(j);
                Set<DependencyType> allCovered = new HashSet<>(Arrays.asList(dj.first.getCoveredDependencies()));
                allCovered.retainAll(Arrays.asList(di.first.getCoveredDependencies()));
                if (dj.second.equals(di.second) && !allCovered.isEmpty()) {
                    trsToAnn.add(j);
                    found = true;
                }
            }
            if (found) {
                trsToAnn.add(i);
            }
        }
        trsToAnn.forEach(i -> {
            Annotation ann = holder.createErrorAnnotation(depTrs.get(i).second,
                    "Dependency Tag conflicts with another tag in the file.");
            ann.registerFix(new RemoveDependencyQuickFix(pkgXml, i));
//            ann.registerFix(new ReformatPackageXmlFix(pkgXml));
        });
    }
}