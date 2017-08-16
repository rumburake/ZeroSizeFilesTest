# ZeroSizeFilesTest

This is a test app which tries to write on all available storages and see if the files created report the proper size.

The problem happens on some FUSE mounts, when newly written files report a 0 (zero) size until the FS is remounted.

You can find more details about the problem here: https://jira.lineageos.org/browse/BUGBASH-814?focusedCommentId=17864&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-17864
