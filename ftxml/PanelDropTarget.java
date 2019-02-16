package ftxml;


import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * allow import of filtered file
 * <p>
 * component must add addPropertyChangeListener to DROP_EVENT<BR>
 * if is multiple file import: the event fired has isModifica true
 **/
public class PanelDropTarget implements DropTargetListener {
	public final static String DROP_EVENT = "DROP";
	String filtro;
	
	/**
	 * allow import of filtered file to a component
	 * @param comp Component
	 * @param filtro String of ending filename
	 */
	public PanelDropTarget(Component comp, String filtro) {
		this.pane = comp;
		this.background = comp.getBackground();
		if(background == null)
			background = new Color(200,200,200);
		this.filtro = filtro;
		// Create the DropTarget and register it with the JPanel.
		dropTarget = new DropTarget(comp, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
	}

	// Implementation of the DropTargetListener interface
	public void dragEnter(DropTargetDragEvent dtde) {
		// Get the type of object being transferred and determine whether it is appropriate.
		checkTransferType(dtde);

		// Accept or reject the drag.
		acceptOrRejectDrag(dtde);
	}

	public void dragExit(DropTargetEvent dte) {
		// DnDUtils.debugPrintln("DropTarget dragExit");
		pane.setBackground(background);
	}

	public void dragOver(DropTargetDragEvent dtde) {
		// Accept or reject the drag
		acceptOrRejectDrag(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		// Accept or reject the drag
		acceptOrRejectDrag(dtde);
	}

	Object last_Drop = null;

	public void drop(DropTargetDropEvent dtde) {
		// Check the drop action
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();
			try {
				boolean result = dropComponent(transferable);
//				System.out.println("dropAction()");
				if (drop != null && drop != last_Drop) {
					pane.firePropertyChange(DROP_EVENT, 0, 1);
//					System.out.println("PanelDropTarget fire drop()"+drop.getClass());
					last_Drop = drop;
				}
				dtde.dropComplete(result);
			} catch (Exception e) {
				dtde.dropComplete(false);
			}
		} else {
			dtde.rejectDrop();
		}
	}

	// Internal methods start here
	protected boolean acceptOrRejectDrag(DropTargetDragEvent dtde) {
		int dropAction = dtde.getDropAction();
		int sourceActions = dtde.getSourceActions();
		boolean acceptedDrag = false;

		// Reject if the object being transferred or the operations available are not acceptable.
		if (!acceptableType || (sourceActions & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
			dtde.rejectDrag();
		} else if ((dropAction & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
			// Not offering copy or move - suggest a copy
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			acceptedDrag = true;
		} else {
			// Offering an acceptable operation: accept
			dtde.acceptDrag(dropAction);
			acceptedDrag = true;
		}
		if(acceptedDrag)
			pane.setBackground(background.brighter());
		return acceptedDrag;
	}

	protected void checkTransferType(DropTargetDragEvent dtde) {
		// Only accept a flavor that returns a Component
		acceptableType = false;
		DataFlavor[] fl = dtde.getCurrentDataFlavors();
		for (int i = 0; i < fl.length; i++) {
//			System.out.println("PanelDropTarget.checkTransferType()"+fl[i].getSubType());
			try {
				Object o = dtde.getTransferable().getTransferData(targetFlavor);
			} catch (UnsupportedFlavorException | IOException e1) { }
			Class<?> dataClass = fl[i].getRepresentationClass();
			if (List.class.isAssignableFrom(dataClass)) {
				boolean res = false;
				try {
					res = checkTarget((List<File>) dtde.getTransferable().getTransferData(targetFlavor));
					// This flavor returns a File - accept it.
				} catch (Exception e) {
					e.printStackTrace();
				}
				targetFlavor = fl[i];
				acceptableType = res;
				break;
			}
		}
	}

	private boolean checkTarget(List<File> listOfFile) {
		try {
			List<File> list = (List<File>) listOfFile;
			boolean res = true;
			for (File f : list) {
				if(f.toString().endsWith(filtro) == false)
					res = false;
			}
//			System.out.println("PanelDropTarget.checkTarget()"+res+" "+filtro);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean dropComponent(Transferable transferable) throws IOException, UnsupportedFlavorException {
		Object o = transferable.getTransferData(targetFlavor);

		try {
			if (o instanceof List) {
				if(checkTarget((List<File>) o)) {
					drop = (List) o;
					return true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected Component pane;
	protected Color background;
	protected DropTarget dropTarget;

	protected boolean acceptableType; // Indicates whether data is acceptable

	protected DataFlavor targetFlavor; // Flavor to use for transfer

	@SuppressWarnings("rawtypes")
	public static List drop;

//	public static void main(String[] args) {
//		JFrame f = new JFrame("dd");
//		JSplitPane spl = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		
//		Vector<Studio> vs = new Vector<>();
//		vs.add(new Studio());
//		YearPlanner yp = new YearPlanner(vs, 1);
//		
//		spl.setLeftComponent(yp);
//		spl.setRightComponent(new JButton("ddddd"));
//		f.getContentPane().add(spl);
//		new PanelDropTarget(yp);
//		f.pack();
//		f.setVisible(true);
//		f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
//		yp.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				try {
//					for (Object object : drop) {
//						System.out.println("PanelDropTarget.dropComponent()" + object + " " + object.getClass());
//					}
//					drop = null;
//				}
//				catch (Exception e) {
//				}
//			}
//		});
//	}
}

