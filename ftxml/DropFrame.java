package ftxml;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import fatturaXML.FatturaXMLreader;

public class DropFrame extends JFrame {
	public DropFrame() {
	}
	private static int X;
	private static int Y;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			boolean altDown = false;
			
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setUndecorated(true);
				
				JPanel p = new JPanel(new BorderLayout());
				new PanelDropTarget(p, ".xml");
				p.setOpaque(true);
				p.setBorder(new LineBorder(Color.DARK_GRAY, 2));
				p.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						try {
							for (Object object : PanelDropTarget.drop) {
								File f = (File) object;
								try {
									FatturaXMLreader.main(new String[]{f.toString()});
								} catch (NullPointerException e) {
									error(p);
								}
							}
							PanelDropTarget.drop = null;
						}
						catch (Exception e) {
						}
					}

				});
				f.getContentPane().add(p);
				f.setBounds(10, 10, 110, 120);
				f.setVisible(true);
				
				JLabel tit = new JLabel(" VEDI Fat. XML");
				tit.setBorder(new EmptyBorder(1, 1, 1, 1));
				JPanel pn = new JPanel(new BorderLayout());
				pn.add(tit);
				JButton b = new JButton("X");
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				b.setFont(b.getFont().deriveFont(7f));
				b.setMargin(new Insets(1, 1, 1, 1));
				b.setPreferredSize(new Dimension(20,20));
				b.setFocusPainted(false);
				pn.add(b, BorderLayout.EAST);
				
				JLabel ps = new JLabel("<html><small><center>Rilascia qui per<br> non vedere allegato");
				ps.setOpaque(true);
				ps.setBackground(Color.LIGHT_GRAY);
				ps.setBorder(new EmptyBorder(2,2,2,2));
				new PanelDropTarget(ps, ".xml");
				ps.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						try {
							for (Object object : PanelDropTarget.drop) {
								File f = (File) object;
								try {
									FatturaXMLreader.main(new String[]{f.toString(), "noatt"});
								} catch (NullPointerException e) {
									error(ps);
								}
							}
							PanelDropTarget.drop = null;
						}
						catch (Exception e) {
						}
					}
				});
				p.add(pn, BorderLayout.NORTH);
				p.add(Box.createGlue());
				p.add(ps, BorderLayout.SOUTH);
				
				f.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						X = e.getX();
						Y = e.getY();
						((Window) e.getSource()).toFront();
					}
				});
				f.addMouseMotionListener(new MouseMotionAdapter() {
					public void mouseDragged(MouseEvent e) {
						if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
							((Window) e.getSource()).setLocation(((Component) e.getSource()).getLocation().x + (e.getX() - X), ((Component) e.getSource()).getLocation().y + (e.getY() - Y));
						}
					}
				});
			}
		});
	}

	private static void error(Component p) {
		JOptionPane.showMessageDialog(p, "File non leggibile", "Errore", JOptionPane.ERROR_MESSAGE);
	}
}