package middle;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class DeployGUI extends JFrame {

	private JPanel contentPane;
	private JTextField name;
	private JTextField lat;
	private JTextField lon;
	private JTextField rmiPort;
	private JTextField port;
	private List<Deploy> ds;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeployGUI frame = new DeployGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DeployGUI() {
		ds = new ArrayList<Deploy>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 584, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		final JTextArea textArea = new JTextArea();
		contentPane.add(textArea, BorderLayout.CENTER);
		
		name = new JTextField();
		name.setText("Name");
		panel.add(name);
		name.setColumns(5);
		
		port = new JTextField();
		port.setText("port");
		panel.add(port);
		port.setColumns(5);
		
		lat = new JTextField();
		lat.setText("Lat");
		panel.add(lat);
		lat.setColumns(5);
		
		lon = new JTextField();
		lon.setText("Long");
		panel.add(lon);
		lon.setColumns(5);
		
		rmiPort = new JTextField();
		rmiPort.setText("RmiPort");
		panel.add(rmiPort);
		rmiPort.setColumns(5);
		
		JButton deploy = new JButton("Create");
		deploy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String _name = name.getText();
					int _port = Integer.parseInt(port.getText());
					double _lat = Double.parseDouble(lat.getText());
					double _lon =  Double.parseDouble(lon.getText());
					int _rmiPort = Integer.parseInt(rmiPort.getText());
					ds.add( new Deploy(_name, _port, _lat, _lon, _rmiPort) );
					textArea.append("Added server: " + _name + ":" + _port + " @(" + _lat + ", " + _lon + ") on rmi: " + _rmiPort + " \n");
					name.setText("Name");
					port.setText("Port");
					lat.setText("Lat");
					lon.setText("Lon");
					rmiPort.setText("RmiPort");					
				} catch (Exception e1) {	
					textArea.append("[!]: " + e1.toString());
					e1.printStackTrace();
				}
			}
		});
		panel.add(deploy);	
	}

}
