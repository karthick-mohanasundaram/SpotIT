package sim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import unit.Unit;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

public class MainWindow {
	Map<String, Unit> units = new HashMap<String, Unit>();
	ExecutorService pool = Executors.newCachedThreadPool();
	
	
	private JFrame frame;
	private JPanel central;
	private JTextField name;
	private JTextField rmiPort;

	/**	
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 529, 319);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		central = new JPanel();
		frame.getContentPane().add(central, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		frame.getContentPane().add(buttons, BorderLayout.NORTH);
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		
		name = new JTextField();
		name.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					addUnit();
			}
		});
		name.setText("Name");
		buttons.add(name);
		name.setColumns(5);
		
		JButton btnAddUnit = new JButton("Add unit");
		btnAddUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addUnit();
			}
		});
		
		rmiPort = new JTextField();
		rmiPort.setText("rmiPort");
		buttons.add(rmiPort);
		rmiPort.setColumns(5);
		buttons.add(btnAddUnit);
		
		JButton btnRefreshList = new JButton("Refresh list");
		btnRefreshList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshFrame();
			}
		});
		buttons.add(btnRefreshList);
	}
	
	private void addUnit() {
		int rmi = Integer.parseInt( rmiPort.getText() ); 
		Unit u = new Unit( rmi, name.getText() );
		units.put(u.getCode(), u);
		pool.execute(new Runnable() {
			private Unit myUnit;
			@Override
			public void run() {
				myUnit.work();
			}
			private Runnable init(Unit u) {
				this.myUnit = u;
				return this;
			}
		}.init(u));
		name.setText("Name");
		refreshFrame();
	}
	
	public void set(Unit u, int status) {
		u.setSensorStatus(status);
		refreshFrame();
	}

	public void delete(Unit u) {
		units.remove(u.getCode());
		refreshFrame();
	}	
	
	private void refreshFrame() {
		central.removeAll();
		for(Unit u : units.values()) {
			UnitPanel p = new UnitPanel(this);
			p.setUnit(u);
			p.getNameLabel().setText( u.getCode() + ":" + u.getRmi() );
			try {
				p.getStatusCombo().setSelectedIndex( u.getSpot().getStatus() );
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			central.add(p);
			p.setVisible(true);
		}
		frame.revalidate();
		frame.repaint();
	}

	public JPanel getCentralPanel() {
		return central;
	}
}
