package ros.integrate.msg;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ROSMsgUtil {
    /**
     * checks of this is an annotation comment.
     * @param comment the psi-element to check
     * @return {@param comment} in {@link ROSMsgComment} form if it is an annotation, <code>null</code> otherwise.
     */
    @Nullable
    @Contract("null -> null")
    public static ROSMsgComment checkAnnotation(@Nullable PsiElement comment) {
        if(comment instanceof ROSMsgComment && comment.getText().startsWith(ROSMsgElementFactory.ANNOTATION_PREFIX)) {
            return (ROSMsgComment) comment;
        }
        return null;
    }

    /**
     * fetches all the message file names in the provided project with additional options for filtering
     * @param project the project where to search for all messages
     * @param key if not null, only finds message files that have this name,
     *            otherwise, finds all messages regardless of their name.
     * @param file if null, will search for all files in the project. If not null, the provided file will be excluded from the search.
     * @return a non-null list of strings containing all the message name found with the query.
     */
    @NotNull
    public static List<String> findMessageNames(@NotNull Project project, @Nullable String key, @Nullable VirtualFile file) {
        List<String> result = new ArrayList<>();
        findMessages(project, key, file).forEach(
                location->result.add(location.getName())
        );
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    /**
     * finds all the message files in the provided project with additional options for filtering
     * @param project the project where to search for all messages
     * @param key if not null, only finds message files that have this name,
     *            otherwise, finds all messages regardless of their name.
     * @param file if null, will search for all files in the project. If not null, the provided file will be excluded from the search.
     * @return a non-null list containing all message files found via the query.
     */
    static List<ROSMsgFile> findMessages(@NotNull Project project, @Nullable String key, @Nullable VirtualFile file) {
        List<ROSMsgFile> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(ROSMsgFileType.INSTANCE, GlobalSearchScope.allScope(project));
        if( file != null) {
            virtualFiles.remove(file);
        }
        for (VirtualFile virtualFile : virtualFiles) {
            ROSMsgFile rosMsgFile = (ROSMsgFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (rosMsgFile != null) {
                if (key == null || key.equals(rosMsgFile.getName())) {
                    result.add(rosMsgFile);
                }
            }
        }
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    /**
     * a useful utility function for trimming the .msg or .srv from the message file name.
     * @param name the string holding the message/service file name.
     * @return the trimmed version of the provided string.
     */
    @NotNull
    public static String trimMsgFileName(@NotNull String name) {
        return name.substring(0,name.length() - 4);
    }
}
