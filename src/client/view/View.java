package client.view;

import javax.swing.*;

import javax.swing.tree.*;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import client.model.Model;

import java.awt.FlowLayout;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;


public class View extends JFrame  {
	private Model model;
	private ActionListener controller;
	
	private JLabel labelUser;
    private JTextField fieldUser;
    
	private JLabel labelPassword;
    private JPasswordField fieldPassword;
	
	private JLabel labelHost;
	private JLabel labelPort;
    private JTextField fieldHost;
    private JTextField fieldPort;
    private JButton buttonDisconnect;
    private JButton buttonConnect;
    
    private JTree tree;
    private JScrollPane treeScroll;
    private DefaultMutableTreeNode mtn;
    private DefaultTreeModel treeModel;

	
	private JFileChooser fileChooserDownload ;
	private JLabel labelSaveTo;
	private JTextField textFieldSaveTo;
	private JButton buttonDownload;
	private JButton buttonBrowse1;
	private JPanel downloadChooser;
	
	private JFileChooser fileChooserUpload;
	private JTextField textFieldFile;
	private JLabel labelFile;
	private JButton buttonUpload;
	private JButton buttonBrowse2;
	private JPanel uploadChooser;
	
	private JLabel labelFileSize;
	private JTextField fieldFileSize;
	private JLabel labelProgress;
	private JProgressBar progressBar;
	
	
	public View() {
		super("Client");
		labelHost = new JLabel("Host:");
		labelPort = new JLabel("Port:");
		fieldHost = new JTextField("127.0.0.1",40);
		fieldPort = new JTextField("11111",5);
		buttonDisconnect = new JButton("Disconnect");
		buttonDisconnect.setEnabled(false);
		buttonConnect = new JButton("Connect");
		
		labelUser = new JLabel("User:");
		fieldUser = new JTextField(40);
		labelPassword = new JLabel("Password:");
		fieldPassword = new JPasswordField(40);
		

		mtn = new DefaultMutableTreeNode("Server");
		treeModel = new DefaultTreeModel(mtn);
		tree = new JTree();
		tree.setModel(null);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				defineChooser(e);
			}
		});
		treeScroll = new JScrollPane(tree);
		treeScroll.setPreferredSize(new Dimension(150,300));


		
		fileChooserDownload = new JFileChooser();
		fileChooserDownload.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		labelSaveTo = new JLabel("Save file to:");
		buttonBrowse1 = new JButton("Browse...");
		downloadChooser = new JPanel();
		downloadChooser.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		textFieldSaveTo = new JTextField(30);
		downloadChooser.add(labelSaveTo);
		downloadChooser.add(textFieldSaveTo);
		downloadChooser.add(buttonBrowse1);
		buttonBrowse1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileChooserDownload.showSaveDialog(downloadChooser) 
                		== JFileChooser.APPROVE_OPTION) {
                	textFieldSaveTo.setText(fileChooserDownload.getSelectedFile().getAbsolutePath());
                }
            }
        });
		
		buttonDownload = new JButton("Download");
		buttonDownload.setEnabled(false);
		
		fileChooserUpload = new JFileChooser();
		labelFile = new JLabel("File to upload:");
		buttonBrowse2= new JButton("Browse...");
		uploadChooser = new JPanel();
		uploadChooser.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		textFieldFile = new JTextField(30);
		uploadChooser.add(labelFile);
		uploadChooser.add(textFieldFile);
		uploadChooser.add(buttonBrowse2);
		buttonBrowse2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileChooserUpload.showSaveDialog(uploadChooser) 
                		== JFileChooser.APPROVE_OPTION) {
                	textFieldFile.setText(fileChooserUpload.getSelectedFile().getAbsolutePath());
                }
            }
        });
		
		buttonUpload = new JButton("Upload");
		buttonUpload.setEnabled(false);
		
		labelFileSize = new JLabel("File size (bytes):");
		fieldFileSize = new JTextField(15);
		fieldFileSize.setEditable(false);
		
		labelProgress = new JLabel("Progress:");
		progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(200, 30));
        progressBar.setStringPainted(true);
		

        
		setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(labelHost, constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        add(fieldHost, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(labelPort, constraints);
        constraints.gridx = 1;
        add(fieldPort, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(labelUser, constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        add(fieldUser, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        add(labelPassword, constraints);
        constraints.gridx = 1;
        add(fieldPassword, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.WEST;//
        constraints.fill = GridBagConstraints.NONE;
        add(buttonConnect, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        add(buttonDisconnect, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        add(treeScroll, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.WEST;
        add(downloadChooser, constraints);
        
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.WEST;
        add(uploadChooser, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        add(buttonDownload, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        add(buttonUpload, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(labelFileSize, constraints);
        
        constraints.gridx = 1;
        add(fieldFileSize, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 9;
        add(labelProgress, constraints);
        
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, constraints);
        pack();
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        uploadChooser.setVisible(false);
	}
	public void defineChooser(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node == null) return;
		if (node.isLeaf()) {
			uploadChooser.setVisible(false);
			buttonUpload.setVisible(false);
			downloadChooser.setVisible(true);
			buttonDownload.setVisible(true);
		} else {
			downloadChooser.setVisible(false);
			buttonDownload.setVisible(false);
			uploadChooser.setVisible(true);
			buttonUpload.setVisible(true);
		}
	}
	
	public void addModel(Model m) {
		model = m;
	}
	public void addController(ActionListener l) {
		controller = l;
        buttonConnect.addActionListener(controller);
        buttonConnect.setActionCommand("Connect");
        buttonDisconnect.addActionListener(controller);
        buttonDisconnect.setActionCommand("Disconnect");
        buttonDownload.addActionListener(controller);
        buttonDownload.setActionCommand("Download");
        buttonUpload.addActionListener(controller);
        buttonUpload.setActionCommand("Upload");
	}
	
	public String getHost() {
		return fieldHost.getText();
	}
	public int getPort() {
		return Integer.parseInt(fieldPort.getText());
	}
	public String getUser() {
		return fieldUser.getText();
	}
	public String getPassword() {
		return new String(fieldPassword.getPassword());
	}
	public String getDownloadFile() {
		System.out.println(tree.getLastSelectedPathComponent().toString());
		return tree.getLastSelectedPathComponent().toString();
	}
	public String getSavePath() {
		return textFieldSaveTo.getText();
	}
	public String getFilePath() {
		return textFieldFile.getText();
	}
	
	public void updateConnect() {
		buttonConnect.setEnabled(false);
		fieldHost.setEditable(false);
		fieldPort.setEditable(false);
		fieldPassword.setEditable(false);
		fieldUser.setEditable(false);
		buttonDisconnect.setEnabled(true);
		buttonDownload.setEnabled(true);
		buttonUpload.setEnabled(true);
		tree.setModel(treeModel);
	}
	
	public void updateDisconnect() {
		buttonConnect.setEnabled(true);
		fieldHost.setEditable(true);
		fieldPort.setEditable(true);
		fieldPassword.setEditable(true);
		fieldUser.setEditable(true);
		buttonDisconnect.setEnabled(false);
		buttonDownload.setEnabled(false);
		buttonUpload.setEnabled(false);
		tree.setModel(null);
	}
	
	public void updateTree() {
		mtn.removeAllChildren();
		for (String s: model.getFiles()) {
			mtn.add(new DefaultMutableTreeNode(s));
		}
		tree.expandPath(new TreePath(mtn));
		treeModel.reload();
		///treeScroll.revalidate();
		///tree.revalidate();
		///treeScroll.repaint();
		///tree.repaint();
		
	}
	public void updateTransferStarted() {
		buttonDownload.setEnabled(false);
		buttonUpload.setEnabled(false);
		fieldFileSize.setText(String.valueOf(model.getFileSize()));
		progressBar.setValue(0);
		progressBar.setMaximum((int)model.getFileSize());
		progressBar.setString(0 + " %");
	}
	public void updateProgress() {
		progressBar.setValue((int)model.getTotalBytesRead());
		progressBar.setString((int)Math.round(progressBar.getPercentComplete() * 100) + " %");
	}
	public void updateTransferFinished() {
		buttonDownload.setEnabled(true);
		buttonUpload.setEnabled(true);
	}
	public void updateUnknownHost() {
		JOptionPane.showMessageDialog(null,
            "Unknown host", "Error",
            JOptionPane.ERROR_MESSAGE);
	}
	public void updateWrongLogin() {
		JOptionPane.showMessageDialog(null,
            "Wrong user/password", "Error",
            JOptionPane.ERROR_MESSAGE);
	}
}
