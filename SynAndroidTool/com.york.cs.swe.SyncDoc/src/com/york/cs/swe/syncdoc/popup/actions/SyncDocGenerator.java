package com.york.cs.swe.syncdoc.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.antlr.runtime.EarlyExitException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.emfatic.core.generator.ecore.EcoreGenerator;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.EvlUnsatisfiedConstraint;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.york.cs.swe.syncdoc.codegeneration.CodeGenerator;
import com.york.cs.swe.syncdoc.views.ValidationView;

public class SyncDocGenerator {

	protected boolean generatePluginXml = true;

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out
					.println("Use: SyncDocGenerator foo.emf or SyncDocGenerator foo.ecore");
			return;
		}

		SyncDocGenerator generator = new SyncDocGenerator();
		generator.generate(new File(args[0]), null);

	}

	public SyncDocGenerator() {
	}

	/*
	 * public SyncDocGenerator(boolean generatePluginXml) {
	 * this.generatePluginXml = generatePluginXml; }
	 */

	protected File outputDirectory = null;

	public boolean generate(File inputFile, IFile selectedFile)
			throws Exception {

		if (inputFile.getName().endsWith(".ecore")) {
			EmfModel model = new EmfModel();
			model.setName("Ecore");
			EPackage.Registry.INSTANCE.put(EcorePackage.eINSTANCE.getNsURI(),
					EcorePackage.eINSTANCE);
			model.setMetamodelUri(EcorePackage.eINSTANCE.getNsURI());
			model.setModelFile(inputFile.getAbsolutePath());
			model.load();

			if (isValidModel(model)) {

				Collection<EObject> packages = model.getAllOfType("EPackage");
				EPackage ePackage = (EPackage) packages.iterator().next();

				File packageFile = generatePackage(ePackage, selectedFile);

				generateSyncAPI(model, packageFile);

				generateConfigFile(model,
						createConfigFileDirIfNeeded(inputFile));
				model.disposeModel();
				return true;
			} else {
				model.disposeModel();
				return false;

			}

		} else if (inputFile.getName().endsWith(".emf")) {
			EcoreGenerator ecoreGenerator = new EcoreGenerator();
			ecoreGenerator.generate(inputFile, true);
			return generate(
					new File(inputFile.getAbsolutePath().replaceAll("\\.emf$",
							".ecore")), selectedFile);
		}
		throw new EarlyExitException();

	}

	private File generatePackage(EPackage ePackage, IFile selectedFile)
			throws ParserConfigurationException, SAXException, IOException,
			CoreException {

		IContainer container = selectedFile.getProject();
		final IFolder srcGenFolder = container.getFolder(new Path(
				ContentValues.SRC_GEN_PATH));
		// folder no exists create
		if (!srcGenFolder.exists()) {
			try {
				createSrcGen(container);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String packageName = ePackage.getName();

		IProject project = selectedFile.getProject();
		IJavaProject javaProject = null;
		if (project.hasNature(JavaCore.NATURE_ID)) {
			javaProject = JavaCore.create(project);

		}

		IPackageFragmentRoot srcFolder = javaProject
				.getPackageFragmentRoot(srcGenFolder);

		IPackageFragment fragment = srcFolder.createPackageFragment(
				packageName, true, null);

		if (!fragment.exists())
			fragment.save(null, true);

		String packageFolderPath = fragment.getResource().getLocation()
				.toOSString();
		File packageFolder = new File(packageFolderPath);

		return packageFolder;

	}

	private File createConfigFileDirIfNeeded(File inputFile) {
		

		File configFolder = new File(inputFile.getParentFile(),
				"ConfigurationFiles");
		if (!configFolder.exists()) {
			configFolder.mkdir();
		}

		
		return configFolder;
	}

	private boolean isValidModel(EmfModel model) throws Exception {

		EvlModule evlModule = new EvlModule();

		try {
			evlModule.parse(CodeGenerator.class.getResource(
					"modelValidation.evl").toURI());
			evlModule.getContext().getModelRepository().addModel(model);

			evlModule.execute();

			List<EvlUnsatisfiedConstraint> evlUnsatisfiedConstraints = evlModule
					.getContext().getUnsatisfiedConstraints();

			Collection<EObject> packages = model.getAllOfType("EPackage");
			EPackage ePackage = (EPackage) packages.iterator().next();

			ValidationView
					.setEvlUnsatisfiedConstraints(evlUnsatisfiedConstraints);
			ValidationView.setModelName(ePackage.getName());

			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			IViewPart view = page.findView(ValidationView.ID);

			page.showView(ValidationView.ID);

			view.setFocus();

			// no errors
			if (evlUnsatisfiedConstraints.size() == 0) {

				return true;

			} else {
				return false;
			}

		} catch (Exception e) {

			e.printStackTrace();
			throw new Exception("Error while validating model");
		}

	}

	protected void generateSyncAPI(EmfModel model, File directory)
			throws Exception {
		System.out.println("******************************0-");

		EglFileGeneratingTemplateFactory templateFactory = new EglFileGeneratingTemplateFactory();

		templateFactory.setOutputRoot(directory.getAbsolutePath());

		EgxModule egxModule = new EgxModule(templateFactory);

		egxModule.parse(CodeGenerator.class.getResource("coordinator.egx")
				.toURI());

		egxModule.getContext().getModelRepository().addModel(model);

		egxModule
				.getContext()
				.getFrameStack()
				.put(Variable.createReadOnlyVariable("generatePluginXml",
						generatePluginXml));

		egxModule.execute();

	}

	protected void generateConfigFile(EmfModel model, File directory) throws Exception {

		EglFileGeneratingTemplateFactory templateFactory = new EglFileGeneratingTemplateFactory();

		templateFactory.setOutputRoot(directory.getAbsolutePath());

		EgxModule egxModule = new EgxModule(templateFactory);

		egxModule.parse(CodeGenerator.class.getResource(
				"coordinatorConfigFiles.egx").toURI());

		egxModule.getContext().getModelRepository().addModel(model);

		egxModule
				.getContext()
				.getFrameStack()
				.put(Variable.createReadOnlyVariable("generatePluginXml",
						generatePluginXml));

		egxModule.execute();
		egxModule.getContext().getModelRepository().dispose();

	}

	protected File getPluginXmlFile(File directory) {
		if (containsFile(directory, "plugin.xml")
				|| containsFile(directory, "META-INF")) {
			return new File(directory.getAbsolutePath() + "/plugin.xml");
		} else if (directory.getParentFile() != null) {
			return getPluginXmlFile(directory.getParentFile());
		}
		return null;
	}

	protected boolean containsFile(File directory, String file) {
		for (String f : directory.list()) {
			if (f.equals(file))
				return true;
		}
		return false;
	}

	private void createSrcGen(final IContainer container) throws Exception {
		final IFolder folder = container.getFolder(new Path(
				ContentValues.SRC_GEN_PATH));
		if (!folder.exists()) {
			folder.create(true, true, null);
		}
		addSrcGenFolderToClasspath(container);
	}

	private void addSrcGenFolderToClasspath(IContainer container)
			throws Exception {
		IFile classpath = container.getFile(new Path(".classpath"));

		if (getSourceFolderPathFromClasspath(classpath.getContents()) == null) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(classpath.getContents());

			Element element = doc.createElement("classpathentry");
			element.setAttribute("kind", "src");
			element.setAttribute("path", ContentValues.SRC_GEN_PATH);

			doc.getElementsByTagName("classpath").item(0).appendChild(element);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Source xmlSource = new DOMSource(doc);
			Result outputTarget = new StreamResult(outputStream);
			TransformerFactory.newInstance().newTransformer()
					.transform(xmlSource, outputTarget);

			classpath.setContents(
					new ByteArrayInputStream(outputStream.toByteArray()), true,
					true, null);
		}
	}

	private String getSourceFolderPathFromClasspath(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(is);
		NodeList classpathEntries = doc.getElementsByTagName("classpathentry");
		for (int i = 0; i < classpathEntries.getLength(); i++) {
			NamedNodeMap map = classpathEntries.item(i).getAttributes();
			if (map.getNamedItem("kind").getNodeName().equals("kind")
					&& map.getNamedItem("kind").getNodeValue().equals("src")) {
				if (map.getNamedItem("path").getNodeValue()
						.equals(ContentValues.SRC_GEN_PATH)) {
					return map.getNamedItem("path").getNodeValue();
				}
			}
		}
		return null;
	}

}
