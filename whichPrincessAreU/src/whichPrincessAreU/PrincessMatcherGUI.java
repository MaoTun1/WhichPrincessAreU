package whichPrincessAreU;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class PrincessMatcherGUI {

    private JLabel userPhotoLabel;
    private JLabel resultPhotoLabel;
    private PrincessMatcher matcher;
    private String userPhotoPath = "src/user/user.png"; // Kullanıcı fotoğrafı için hedef path

    public PrincessMatcherGUI() {
        matcher = new PrincessMatcher();

        JFrame frame = new JFrame("Which Princess Are You?");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        userPhotoLabel = new JLabel("Drop an Image Here", SwingConstants.CENTER);
        userPhotoLabel.setBounds(50, 150, 200, 200);
        userPhotoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        frame.add(userPhotoLabel);

        enableDragAndDrop(userPhotoLabel);

        JLabel titleLabel = new JLabel("Which Princess Are You?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(200, 20, 400, 30);
        frame.add(titleLabel);

        resultPhotoLabel = new JLabel();
        resultPhotoLabel.setBounds(550, 150, 200, 200);
        resultPhotoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        frame.add(resultPhotoLabel);

        JButton runButton = new JButton("Run");
        runButton.setBounds(350, 400, 100, 30);
        frame.add(runButton);

        runButton.addActionListener(e -> {
            String imagePath = "src/images";
            String haarCascadePath = "src/resources/haarcascade_frontalface_default.xml";

            String bestMatchPrincess = matcher.findMostSimilarPrincess(userPhotoPath, imagePath, haarCascadePath);
            if (bestMatchPrincess != null) {
                ImageIcon resultImage = new ImageIcon(imagePath + "/" + bestMatchPrincess);
                Image scaledResultImage = resultImage.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                resultPhotoLabel.setIcon(new ImageIcon(scaledResultImage));
            } else {
                JOptionPane.showMessageDialog(frame, "No match found!");
            }
        });

        frame.setVisible(true);
    }

    private void enableDragAndDrop(JLabel label) {
        new DropTarget(label, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {}

            @Override
            public void dragOver(DropTargetDragEvent dtde) {}

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}

            @Override
            public void dragExit(DropTargetEvent dte) {}

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable()
                        .getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);

                    if (!droppedFiles.isEmpty()) {
                        File file = droppedFiles.get(0);
                        if (file.getName().matches(".*\\.(png|jpg|jpeg|gif)")) {
                            // Kullanıcı fotoğrafını src/user/user.png yoluna kopyala
                            File destinationFile = new File(userPhotoPath);
                            Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            // Fotoğrafı GUI'de göster
                            ImageIcon imageIcon = new ImageIcon(destinationFile.getAbsolutePath());
                            Image scaledImage = imageIcon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
                            label.setIcon(new ImageIcon(scaledImage));
                            label.setText("");
                        } else {
                            JOptionPane.showMessageDialog(null, "Please drop an image file!", "Invalid File", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrincessMatcherGUI::new);
    }
}
