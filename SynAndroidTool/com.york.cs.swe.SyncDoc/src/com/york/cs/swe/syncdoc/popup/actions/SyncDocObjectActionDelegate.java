package com.york.cs.swe.syncdoc.popup.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SyncDocObjectActionDelegate implements IObjectActionDelegate {

	private Shell shell;
	protected ISelection selection = null;

	/**
	 * Constructor for Action1.
	 */
	public SyncDocObjectActionDelegate() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		try {
			// get selected file
			IFile selectedFile = (IFile) ((IStructuredSelection) selection)
					.getFirstElement();

			SyncDocGenerator generator = new SyncDocGenerator();

			if (generator.generate(new File(selectedFile.getLocation()
					.toOSString()), selectedFile)) {
				MessageDialog
						.openConfirm(shell,
								"Model API was generated succesfully!",
								"Please check folder src-gen to see the generated classes");
			} else {
				MessageDialog
						.openError(
								shell,
								"Errors were found in the model!",
								"Please please correct errors before generating code. The list of errors can be seen in view SyncAndroid");
			}

			selectedFile.getProject().refreshLocal(IFile.DEPTH_INFINITE,
					new NullProgressMonitor());
		} catch (Exception e) {
			MessageDialog.openError(shell, "Error", "Sorry, unexpected. Pleasy review your model");
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
