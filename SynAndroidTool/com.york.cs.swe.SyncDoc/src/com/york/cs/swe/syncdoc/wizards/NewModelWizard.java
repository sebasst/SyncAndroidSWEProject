package com.york.cs.swe.syncdoc.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import com.york.cs.swe.syncdoc.Activator;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "emf". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class NewModelWizard extends Wizard implements INewWizard {
	private NewModelWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for NewModelWizard.
	 */
	public NewModelWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new NewModelWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		 monitor.beginTask("Creating " + fileName, 2);
	        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	        IResource resource = root.findMember(new Path(containerName));

	        if (!resource.exists() || !(resource instanceof IContainer)) {
	            throwCoreException("Container \"" + containerName + "\" does not exist.");
	        }

	        IContainer container = (IContainer) resource;

	        final IFolder folder = container.getFolder(new Path("model"));
	        if (!folder.exists()) {
	            folder.create(true, true, monitor);
	        }

	        final IFile emfFile = folder.getFile(fileName);
	        try {
	            InputStream stream = getDefaultFileContent();
	            if (emfFile.exists()) {
	                emfFile.setContents(stream, true, true, monitor);
	            } else {
	                emfFile.create(stream, true, monitor);
	            }
	            stream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	       

	        monitor.worked(1);
		
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, emfFile, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	
    private InputStream getDefaultFileContent() {
        try {
            return Activator.getDefault().getBundle().getEntry("/DefaultFiles/model/DatabaseModel").openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
	/**
	 * We will initialize file contents with a sample text.
	 */

	@SuppressWarnings("unused")
	private InputStream openContentStream() {
		String contents =
			"This is the initial file contents for *.emf file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "com.york.cs.swe.SyncDoc", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	

}