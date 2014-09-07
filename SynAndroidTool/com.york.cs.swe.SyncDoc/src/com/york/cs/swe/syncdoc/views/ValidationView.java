package com.york.cs.swe.syncdoc.views;




import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.evl.EvlUnsatisfiedConstraint;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ValidationView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.york.cs.swe.syncdoc.views.ValidationView";

	private TableViewer viewer;

	Table table;
	public static  List<EvlUnsatisfiedConstraint>  evlUnsatisfiedConstraints =null;
	public static String modelName =null;
	
	

	/**
	 * The constructor.
	 */
	public ValidationView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		System.out.println("view******************************01");
		
		createTable(parent);
		
		
		refresh();
	}

	
	private void createTable(Composite parent) {
		table = new Table(parent, SWT.FULL_SELECTION | SWT.V_SCROLL|SWT.H_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		TableColumn column2 = new TableColumn(table, SWT.LEFT);
		System.out.println("view******************************02");

		column1.setWidth(100);
		column1.setText("Type");
		column2.setWidth(100);
		column2.setText("Message");
		 
		}
	
	public void refresh(){
	
		System.out.println("view******************************03");
		table.removeAll();
		
		System.out.println("view******************************04");
		
		if(evlUnsatisfiedConstraints==null){
			evlUnsatisfiedConstraints=new ArrayList<EvlUnsatisfiedConstraint>();
			EvlUnsatisfiedConstraint evl = new EvlUnsatisfiedConstraint();
			
			
		}
		System.out.println("view******************************041 "+evlUnsatisfiedConstraints.size());
		for(EvlUnsatisfiedConstraint evlUC : evlUnsatisfiedConstraints){
			TableItem row1 = new TableItem(table, SWT.ERROR);
			row1.setText(new String[] { "Error", evlUC.getMessage() });
			
		}
		
		System.out.println("view******************************05");
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"SyncAndroid",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	
		refresh();
		
		
		
		table.setFocus();
	}

	public  static List<EvlUnsatisfiedConstraint> getEvlUnsatisfiedConstraints() {
		return evlUnsatisfiedConstraints;
	}

	public  static void setEvlUnsatisfiedConstraints(
			List<EvlUnsatisfiedConstraint> evlUnsatisfiedConstraints) {
		ValidationView.evlUnsatisfiedConstraints = evlUnsatisfiedConstraints;
		
	}

	public static String getModelName() {
		return  modelName;
	}

	public  static void setModelName(String modelName) {
		ValidationView.modelName = modelName;
	}
}