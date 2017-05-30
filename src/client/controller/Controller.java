package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import client.view.View;
import java.util.Observer;
import java.util.Observable;
import client.model.Model;

public class Controller implements ActionListener, Observer {
	private Model model;
	private View view;
	
	public Controller(Model m, View v) {
		model = m;
		model.addObserver(this);
		view = v;
		view.addController(this);
		view.addModel(model);
		view.setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "Connect":
			new Thread() {
				public void run() {
					model.connect(view.getHost(), view.getPort(), view.getUser(), view.getPassword());	
				}
			}.start();
			break;
		case "Disconnect":
			new Thread() {
				public void run() {
					model.disconnect();	
				}
			}.start();
			break;
		case "Download":
			new Thread() {
				public void run() {
					model.download(view.getDownloadFile(), view.getSavePath());
				}
			}.start();
			break;
		case "Upload":
			new Thread() {
				public void run() {
					model.upload(view.getFilePath());
				}
			}.start();
			break;
		}
	}
	
	public void update(Observable obj, Object arg) {
		switch((String)arg) {
		case "Connect":
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	view.updateConnect();
			    }
			  });
			break;
		case "Disconnect":
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	view.updateDisconnect();
			    }
			  });
			break;
		case "Tree":
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	view.updateTree();
			    }
			  });
			break;
		case "TransferStarted":
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	view.updateTransferStarted();
			    }
			  });
			break;
		case "Progress":
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	view.updateProgress();
			    }
			  });
			break;
		case "TransferFinished":
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	view.updateTransferFinished();
			    }
			  });
			break;
		case "UnknownHost":
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						view.updateUnknownHost();
					}
				});
			} catch(Exception e) {}
			break;
		case "WrongLogin":
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						view.updateWrongLogin();
					}
				});
			} catch(Exception e) {}
			break;
		}
	}
	
	public static void main(String args []){
		SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		        View view = new View();
		        Model model = new Model();
		        new Controller(model, view);
		      }
		});
	}
}
