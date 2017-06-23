package sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import unit.Unit;

public class UnitPanel extends JPanel {
	private JLabel name;
	private Unit unit;
	private MainWindow mw;
	private JComboBox status;
	
	/**
	 * Create the panel.
	 */
	public UnitPanel(MainWindow mw2) {
		this.mw = mw2;
		
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setLayout(new BorderLayout(0, 0));
		
		name = new JLabel("Name");
		name.setHorizontalAlignment(SwingConstants.CENTER);
		add(name, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		add(panel, BorderLayout.CENTER);
		
		JLabel lblStatus = new JLabel("status:");
		panel.add(lblStatus);
		
		status = new JComboBox();
		status.setModel(new DefaultComboBoxModel(new String[] {"INIT", "FREE", "OCCUPIED", "BOOKED", "UNAVAIL", "UNKNOWN", "ERROR"}));
		panel.add(status);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.SOUTH);
		
		JButton btnSet = new JButton("Set");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mw.set(unit, status.getSelectedIndex());
			}
		});
		panel_1.add(btnSet);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mw.delete(unit);
			}
		});
		panel_1.add(btnDelete);

	}

	public JLabel getNameLabel() {
		return name;
	}
	
	public void setUnit(Unit u) {
		this.unit = u;
	}
	public JComboBox getStatusCombo() {
		return status;
	}
}

