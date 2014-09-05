import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.ext.git.client.GitServiceClient;
    private       AppContext                appContext;
    /** Create presenter. */
                                  AppContext appContext,
        this.appContext = appContext;
        service.log(appContext.getCurrentProject().getRootProject(), false,
            listOpenedFiles.add(partPresenter.getEditorInput().getFile().getPath());
        service.diff(appContext.getCurrentProject().getRootProject(), listFiles, DiffRequest.DiffType.RAW, true, 0, commit, false,
        service.reset(appContext.getCurrentProject().getRootProject(), selectedRevision.getId(), finalType, new AsyncRequestCallback<Void>() {
                if (ResetRequest.ResetType.HARD.equals(finalType) || ResetRequest.ResetType.MERGE.equals(finalType)) {
                    // Only in the cases of <code>ResetRequest.ResetType.HARD</code>  or <code>ResetRequest.ResetType.MERGE</code>
        final String projectPath = appContext.getCurrentProject().getRootProject().getPath();
        eventBus.fireEvent(new RefreshProjectTreeEvent());
        for (EditorPartPresenter partPresenter : openedEditors) {
            final FileNode file = partPresenter.getEditorInput().getFile();

            // get project-relative file's path
            final String filePath = file.getPath().substring(projectPath.length() + 1, file.getPath().length());
            if (diff.contains(filePath)) {
                int firstIndex = diff.indexOf(filePath);
                int lastIndex = diff.lastIndexOf(filePath);
                String between = diff.substring(firstIndex, lastIndex);

                if (between.contains("new file mode")) {
                    //<code>diff</code> contains the string "new file mode" in the case if working tree has file
                    // that is not exist in the commit to reset. So this file is necessary to close.
                    eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));
                } else {
                    //File is changed in the commit to reset, so this file is necessary to refresh
                    updateOpenedFile(partPresenter);
        }
     *         file to refresh
    private void updateOpenedFile(EditorPartPresenter partPresenter) {
        try {
            EditorInput editorInput = partPresenter.getEditorInput();
            partPresenter.init(editorInput);
        } catch (EditorInitException event) {
            Log.error(ResetToCommitPresenter.class, "can not initializes the editor with the given input " + event);
        }