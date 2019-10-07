/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Arsalan Farrell
 */
public class NoteController {

    private final NoteGUI view;

    private final List<Integer> list = new ArrayList<>();

    public NoteController(NoteGUI view) {
        this.view = view;

        this.view.getReadBtn().addActionListener((ActionEvent e) -> {
            try {
                process();
            } catch (BadLocationException ex) {
                Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        this.view.getSaveBtn().addActionListener((ActionEvent e) -> {
            save();
        });
    }

    private void save() {
        JFileChooser loadFile = view.getLoadFile();
        if (JFileChooser.APPROVE_OPTION == loadFile.showSaveDialog(view)) {
            BufferedWriter writer = null;
            try {
                String contents = view.getTxtPane().getText();
                if (contents != null && !contents.isEmpty()) {
                    writer = new BufferedWriter(new FileWriter(loadFile.getSelectedFile()));
                    writer.write(contents);
                }
                JOptionPane.showMessageDialog(null, "Data berhasil di-save");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(NoteGUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Data gagal di-save");
            } catch (IOException ex) {
                Logger.getLogger(NoteGUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Data gagal di-save");
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                        view.getTxtPane().setText("");
                    } catch (IOException ex) {
                        Logger.getLogger(NoteGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void process() throws FileNotFoundException, BadLocationException, IOException {
        JFileChooser loadFile = view.getLoadFile();
        StyledDocument doc = view.getTxtPane().getStyledDocument();
        if (JFileChooser.APPROVE_OPTION == loadFile.showOpenDialog(view)) {
            PushbackReader reader = new PushbackReader(new InputStreamReader(new FileInputStream(loadFile.getSelectedFile())));
            PushbackInputStream lineRead = new PushbackInputStream(new FileInputStream(loadFile.getSelectedFile()));
            
            double size = loadFile.getSelectedFile().length();
            char[] words = new char[(int) size];
            

            try {
                reader.read(words);

                String data = null;
                doc.insertString(0, "", null);

                int characterCount = 0;
                int wordCount = 0;
                int count = 0;

                if ((data = new String(words)) != null) {
                    String[] wordlist = data.split("\\s+");

                    byte[] c = new byte[(byte)size];
                    int readChars = 0;
                    boolean empty = true;
                    while ((readChars = lineRead.read(c)) != -1) {
                        empty = false;
                        for (int i = 0; i < readChars; ++i) {
                            if (c[i] == '\n') { 
                               ++count;
                            }
                        }
                    }
                    
                    characterCount += words.length;
                    wordCount += wordlist.length;
                    
                    doc.insertString(doc.getLength(), "" + data + "\n", null);

                }
                
                lineRead.close();
                JOptionPane.showMessageDialog(null, "File Berhasil dibaca." + "\n"
                        + "Jumlah baris = " + count + "\n"
                        + "Jumlah kata = " + characterCount + "\n"
                        + "Jumlah karakter = " + wordCount);

            } catch (IOException ex) {
                Logger.getLogger(NoteController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
