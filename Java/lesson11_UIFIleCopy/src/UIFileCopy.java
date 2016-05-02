import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;


public class UIFileCopy {

	String fileName;
	String directory;
	JFrame mainFrame;
	JProgressBar progressBar;
	JPanel panel; 
	JButton start;
	JButton cancel;
	JLabel currentFile;
	JLabel labelTotal;
	JLabel curTime;
	JLabel remTime;
	JLabel midSpeed;
	JLabel curSpeed;
	Loader loader;
	Font mainFont;
	JScrollPane mainScroll;
	Timer timer;
	long time;
	long speed;
	boolean work = false;
	boolean conti = true;
	List<JLabel> labels = new ArrayList<>();
	
	JLabel addLabel(String text, JPanel p) {
		if (p.equals(panel)) {
			p.add(Box.createVerticalStrut(20));
		}
		JLabel label = new JLabel(text);
		label.setAlignmentX(JComponent.LEFT_ALIGNMENT);
//		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setFont(mainFont);
		p.add(label);
		labels.add(label);
		return label;
	}

	int goodFont(int w, int h) {
		for (int font = 10; font < 100; font++) {
			boolean good = true;
			for (JLabel lab : labels) {
				if (lab.getWidth() / mainFont.getSize() * font > w) {
					good = false;
					break;
				}
				if (lab.getHeight() / mainFont.getSize() * font > h) {
					good = false;
					break;
				}
			}
			if (!good) return font - 5;
		}
		return w * h / 25000;
	}
	public UIFileCopy(String fileName, String to) {
		this.fileName = fileName;
		this.directory = to;		
		mainFont = new Font("Arial", Font.BOLD, 20);
		mainFrame = new JFrame("Copy file");
		panel = new JPanel();
		BoxLayout bb = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bb);		
		addLabel("FROM: " + fileName, panel);
		addLabel("TO: " + to, panel);		
		panel.add(Box.createVerticalStrut(20));
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setSize(new Dimension(1000, 500));
//		UIManager.put("ProgressBar.background", Color.ORANGE);
//		UIManager.put("ProgressBar.HorizontalSize", 100);
//        SwingUtilities.updateComponentTreeUI(mainFram);
		panel.add(progressBar);

		start = new JButton("COPY");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startCopy();
			}
		});
		start.setPreferredSize(new Dimension(200, 100));
		cancel = new JButton("CANCEL");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (work) {
					conti = false;
					JOptionPane warning = new JOptionPane();
					warning.setFont(mainFont);
					warning.setVisible(true);
					if (JOptionPane.showConfirmDialog(mainFrame, "Are you sure to cancel loading?") == 
							JOptionPane.YES_OPTION) {
						work = false;
						loader.cancel(false);
						System.exit(0);
					} else {
						conti = true;
					}
				}
			}
		});
//		cancel.setPreferredSize(new Dimension(200, 100));
		
		JPanel bPanel = new JPanel();
		bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.LINE_AXIS));
//		bPanel.setPreferredSize(new Dimension(400, 100));
		bPanel.add(start);
//		bPanel.add(Box.createHorizontalStrut(10));
		bPanel.add(cancel);
		bPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		panel.add(Box.createVerticalStrut(20));
		panel.add(bPanel);
		
		currentFile = addLabel("FILE: " + fileName, panel);
		labelTotal = addLabel("TOTAL: ", panel);

		JPanel tPanel = new JPanel();
		tPanel.setLayout(new BoxLayout(tPanel, BoxLayout.X_AXIS));
		tPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		curTime = addLabel("CURRENT: ", tPanel);
		tPanel.add(Box.createHorizontalStrut(50));
		remTime = addLabel("REMIND: ", tPanel);
//		remTime.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(tPanel);
		
		JPanel sPanel = new JPanel();
		sPanel.setLayout(new BoxLayout(sPanel, BoxLayout.X_AXIS));
		sPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		midSpeed = addLabel("MIDDLE: ", sPanel);
		sPanel.add(Box.createHorizontalStrut(50));
		curSpeed = addLabel("CURRENT: ", sPanel);
		panel.add(sPanel);
		
		mainScroll = new JScrollPane(panel);
		mainScroll.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = e.getComponent();
				int h = c.getHeight();
				int w = c.getWidth();
				mainFont = new Font(mainFont.getFontName(), mainFont.getStyle(), Integer.max(14, goodFont(w, h)));
				for (JLabel lab : labels) {
					lab.setFont(mainFont);
				}
				start.setFont(mainFont);
				cancel.setFont(mainFont);
				progressBar.setFont(mainFont);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		mainFrame.getContentPane().add(mainScroll, BorderLayout.CENTER);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.pack();
		mainFrame.setBounds(1000, 500, 1000, 500);
		mainFrame.setVisible(true);
	}

	void startCopy() {
		loader = new Loader(fileName, directory);
		loader.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					progressBar.setValue((int)evt.getNewValue());
					System.out.println((int)evt.getNewValue());
				}
			}
		});
		loader.execute();
	}

	public static void main(String[] args) {
		String fileName = args[0];
		String to = args[1];
		new UIFileCopy(fileName, to);
	}

	private class Loader extends SwingWorker<String, String> {
		String fileName;
		String dir;
		long total = 0;
		long fileSize = 0;

		public Loader(String from, String to) {
			this.fileName = from;
			this.dir = to;
		}
		@Override
		protected String doInBackground() throws Exception {
			File fileFrom = new File(fileName);
			File dirTo = new File(dir);
			if (!fileFrom.exists()) {
				JOptionPane warning = new JOptionPane();
				warning.setPreferredSize(new Dimension(500, 300));
				JOptionPane.showMessageDialog(mainFrame, "File doesn't exist");
				warning.setVisible(true);
				return null;				
			}
			if (dirTo.isFile()) {
				JOptionPane warning = new JOptionPane();
				warning.setPreferredSize(new Dimension(500, 300));
				JOptionPane.showMessageDialog(mainFrame, "Sorry, I can't copy to file");
				warning.setVisible(true);
				return null;
			}
			if (fileFrom.isDirectory()	) {
				Files.walkFileTree(Paths.get(fileName), new MyFileVisitor(Paths.get(fileName), Paths.get(dir, fileFrom.getName())));
			} else {
				Files.walkFileTree(Paths.get(fileName), new MyFileVisitor(Paths.get(fileName), Paths.get(dir, fileFrom.getName())));
			}
			return null;
		}
		
		String getTime(long time) {
			long h = time / 3600000;
			long m = (time % 3600000) / 60000;
			long s = (time % 60000) / 1000;
			return h + ":" + m + ":" + s;
			
		}
		
		long getKbytes(long b) {
			return b / 1024;
		}
		void copyFile(File fFrom, File fTo) throws FileNotFoundException {
			work = true;
			conti = true;
			total = 0;
			AtomicInteger length = new AtomicInteger(0);
			AtomicLong tSt = new AtomicLong(0);
			timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setProgress((int)(total * 100 / fileSize));
					labelTotal.setText("TOTAL: " + getKbytes(total) + " from " + getKbytes(fileSize) + " Kb");
					labelTotal.updateUI();
//					System.out.println(time.getTime() + " " + tSt);
					long curS = length.get() / (System.currentTimeMillis() - tSt.get() + 1) * 1000;
					curSpeed.setText("CURRENT: " + getKbytes(curS) + " Kb / second");
					curSpeed.updateUI();
					speed = (speed + curS) / 2;
					midSpeed.setText("MIDDLE: " + getKbytes(speed) + " Kb / second");
					midSpeed.updateUI();
					curTime.setText("TIME: " + getTime((System.currentTimeMillis() - time)));
					curTime.updateUI();
					remTime.setText("REM: " + getTime((fileSize - total) / (speed / 1024)));
					remTime.updateUI();
					timer.start();
				}
			});
			timer.start();
			if (!fTo.exists()) fTo = new File(fTo.getAbsolutePath());
			System.out.println(fFrom.exists() + "  " + fTo.exists() + " " + fFrom.getName());
			fileSize = fFrom.length();
			labelTotal.setText("0" + " from " + getKbytes(fileSize) + " Kb");
			labelTotal.updateUI();
			currentFile.setText("COPY " + fFrom.getName() + " TO " + fTo.getName());
			currentFile.updateUI();
			time = System.currentTimeMillis();
			curTime.setText("TIME: " + getTime((System.currentTimeMillis() - time)));
			FileInputStream from = new FileInputStream(fFrom);
			try {
				FileOutputStream to = new FileOutputStream(fTo);
				try {
					byte[] buffer = new byte[100000];
					length.set(1);;
					while (work && length.get() > 0) {
						if (conti) {
							try {
								tSt.set(System.currentTimeMillis());
								length.set(from.read(buffer));
								to.write(buffer, 0, length.get());
								total += length.get();
							} catch (IOException e) {
								JOptionPane warning = new JOptionPane();
								warning.setPreferredSize(new Dimension(300, 200));
								JOptionPane.showMessageDialog(mainFrame, "Oops..error");
								warning.setVisible(true);
								return;
							}
						}
					}
				} finally {
					try {
						to.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} finally {
				try {
					from.close();
					work = false;
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			work = false;
			return;
		}

		private class MyFileVisitor extends SimpleFileVisitor<Path> {
			private Path from;
			private Path to;

			public MyFileVisitor(Path from, Path to) { 
				this.from = from; 
				this.to = to; 
			} 

			public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) { 
				Path newd = to.resolve(from.relativize(path));
				try {
					System.out.println("FROM: " + path.toString());
					System.out.println("TO: " + newd.toString());
					if (newd.toFile().exists()) {
						JOptionPane warning = new JOptionPane();
						warning.setFont(mainFont);
						warning.setVisible(true);
						if (JOptionPane.showConfirmDialog(mainFrame, "File " + newd.getFileName().toString() + " has already existed. Do you want to overwrite it?") == 
								JOptionPane.YES_OPTION) {
//							Files.delete(newd);
							copyFile(path.toFile(), newd.toFile());
						} else {
							return FileVisitResult.SKIP_SUBTREE;
						}
					} else {
						copyFile(path.toFile(), newd.toFile());
					}
				} catch (IOException e) { 
					e.printStackTrace(); 
				} 
				return FileVisitResult.CONTINUE; 
			} 

			void deleteDirectory(Path file) {
				if (!file.toFile().exists()) {
					return;
				}
				if (file.toFile().isDirectory()) {
					for (File f : file.toFile().listFiles()) {
						deleteDirectory(f.toPath());
					}
					try {
						Files.delete(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Files.delete(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes fileAttributes) { 
				Path newd = to.resolve(from.relativize(path));
				if (newd.toFile().exists()) {
					JOptionPane warning = new JOptionPane();
					warning.setFont(mainFont);
					warning.setVisible(true);
					if (JOptionPane.showConfirmDialog(mainFrame, "Directory " + newd.getFileName().toString() + " has already existed. Do you want to overwrite it?") == 
							JOptionPane.YES_OPTION) {
						try {
							//								Files.createDirectory(newd, fileAttributes);
							deleteDirectory(newd);
							Files.copy(path, newd, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						return FileVisitResult.SKIP_SUBTREE;
					}
				} else {
					try {
						Files.copy(path, newd, StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("DIR: " + newd.toString());
				return FileVisitResult.CONTINUE; 
			} 
		}
	}

}