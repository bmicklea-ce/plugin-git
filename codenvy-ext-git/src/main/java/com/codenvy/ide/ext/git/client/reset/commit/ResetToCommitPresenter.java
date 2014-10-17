        reset();
    /** Reset current HEAD to the specified state and refresh project in the success case.*/
    private void reset() {
        service.reset(appContext.getCurrentProject().getRootProject(), selectedRevision.getId(), finalType,
                      new AsyncRequestCallback<Void>() {
                          @Override
                          protected void onSuccess(Void result) {
                              if (ResetRequest.ResetType.HARD.equals(finalType) || ResetRequest.ResetType.MERGE.equals(finalType)) {
                                  // Only in the cases of <code>ResetRequest.ResetType.HARD</code>  or <code>ResetRequest.ResetType.MERGE</code>
                                  // must change the workdir
                                  refreshProject();
                              }
                              Notification notification = new Notification(constant.resetSuccessfully(), INFO);
                              notificationManager.showNotification(notification);

                          }

                          @Override
                          protected void onFailure(Throwable exception) {
                              String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.resetFail();
                              Notification notification = new Notification(errorMessage, ERROR);
                              notificationManager.showNotification(notification);
                          }
                      });
    /** Refresh project. */
    private void refreshProject() {
            FileNode file = partPresenter.getEditorInput().getFile();
            eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));