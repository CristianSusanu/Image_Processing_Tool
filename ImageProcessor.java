//This is an image processing tool, enabling the use of different image filters

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;


public class ImageProcessor extends Application {

    //Change these values to get a smaller or larger Window.
    private static final int STAGE_WIDTH = 400;
    private static final int STAGE_HEIGHT = 400;

    // Image filters to be implemented
    private static final String[] filterTypes = {"IDENTITY","BLUR", "SHARPEN", "EMBOSS", "EDGE"};

    private Image image;
    private ImageView imgv;
    private VBox vbox;
    private Scene scene;
    private ArrayList<MenuItem> menuItems;
    private String currentFilename;

    public ImageProcessor() {

    }

    public Color[][] applyFilter(Color[][] pixels, float[][] filter) {
        Color[][] result = new Color[pixels.length - 2][pixels[0].length - 2];

        for (int i = 1; i < pixels.length - 1; i++) {
            for (int j = 1; j < pixels[0].length - 1; j++) {
                double newRed = pixels[i - 1][j - 1].getRed() * filter[0][0] + pixels[i - 1][j].getRed()
                                    * filter[0][1] + pixels[i - 1][j + 1].getRed() * filter[0][2]
                                    + pixels[i][j - 1].getRed() * filter[1][0]
                                    +  pixels[i][j].getRed() * filter[1][1] + pixels[i][j + 1].getRed()
                                    * filter[1][2] + pixels[i + 1][j - 1].getRed() * filter[2][0]
                                    + pixels[i + 1][j].getRed() * filter[2][1]
                                    + pixels[i + 1][j + 1].getRed() * filter[2][2];

                if (newRed < 0) {
                    newRed = 0;
                } else if (newRed > 1) {
                    newRed = 1;
                }

                double newGreen = pixels[i - 1][j - 1].getGreen() * filter[0][0] + pixels[i - 1][j].getGreen()
                                     * filter[0][1] + pixels[i - 1][j + 1].getGreen() * filter[0][2]
                                     + pixels[i][j - 1].getGreen() * filter[1][0]
                                     +  pixels[i][j].getGreen() * filter[1][1] + pixels[i][j + 1].getGreen()
                                     * filter[1][2] + pixels[i + 1][j - 1].getGreen() * filter[2][0]
                                     + pixels[i + 1][j].getGreen() * filter[2][1]
                                     + pixels[i + 1][j + 1].getGreen() * filter[2][2];

                if (newGreen < 0) {
                    newGreen = 0;
                } else if (newGreen > 1) {
                    newGreen = 1;
                }

                double newBlue = pixels[i - 1][j - 1].getBlue() * filter[0][0] + pixels[i - 1][j].getBlue()
                                    * filter[0][1] + pixels[i - 1][j + 1].getBlue() * filter[0][2]
                                    + pixels[i][j - 1].getBlue() * filter[1][0]
                                    + pixels[i][j].getBlue() * filter[1][1] + pixels[i][j + 1].getBlue()
                                    * filter[1][2] + pixels[i + 1][j - 1].getBlue() * filter[2][0]
                                    + pixels[i + 1][j].getBlue() * filter[2][1]
                                    + pixels[i + 1][j + 1].getBlue() * filter[2][2];

                if (newBlue < 0) {
                    newBlue = 0;
                } else if (newBlue > 1) {
                    newBlue = 1;
                }

                Color nc = new Color(newRed, newGreen, newBlue, 1.0);

                result[i - 1][j - 1] = nc;
            }
        }

        return result;
    }

    public float[][] createFilter(String filterType) {
        int x = 3;
        int y = 3;

        float[][] filter = new float[x][y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                filter[i][j] = 0;
            }
        }

        if (filterType.equals(filterTypes[0])) {
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    filter[i][j] = 0;
                }
            }
            filter[1][1] = 1;
        } else if (filterType.equals(filterTypes[1])) {
            filter[0][0] = 0.0625f;
            filter[0][1] = 0.125f;
            filter[0][2] = 0.0625f;
            filter[1][0] = 0.125f;
            filter[1][1] = 0.25f;
            filter[1][2] = 0.125f;
            filter[2][0] = 0.0625f;
            filter[2][1] = 0.125f;
            filter[2][2] = 0.0625f;
        } else if (filterType.equals(filterTypes[2])) {
            filter[0][0] = 0;
            filter[0][1] = -1;
            filter[0][2] = 0;
            filter[1][0] = -1;
            filter[1][1] = 5;
            filter[1][2] = -1;
            filter[2][0] = 0;
            filter[2][1] = -1;
            filter[2][2] = 0;
        } else if (filterType.equals(filterTypes[3])) {
            filter[0][0] = -2;
            filter[0][1] = -1;
            filter[0][2] = 0;
            filter[1][0] = -1;
            filter[1][1] = 0;
            filter[1][2] = 1;
            filter[2][0] = 0;
            filter[2][1] = 1;
            filter[2][2] = 2;
        } else if (filterType.equals(filterTypes[4])) {
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    filter[i][j] = -1;
                }
            }
            filter[1][1] = 8;
        }
        return filter;
    }

    public Color[][] applySepia(Color[][] pixels) {
        Color[][] result = new Color[pixels.length][pixels[0].length];

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                double red = pixels[i][j].getRed();
                double green = pixels[i][j].getGreen();
                double blue = pixels[i][j].getBlue();
                double newRed = red * 0.393 + green * 0.769 + blue * 0.189;

                if (newRed < 0) {
                    newRed = 0;
                } else if (newRed > 1) {
                    newRed = 1;
                }

                double newGreen = red * 0.349 + green * 0.686 + blue * 0.168;

                if (newGreen < 0) {
                    newGreen = 0;
                } else if (newGreen > 1) {
                    newGreen = 1;
                }

                double newBlue = red * 0.272 + green * 0.534 + blue * 0.131;

                if (newBlue < 0) {
                    newBlue = 0;
                } else if (newBlue > 1) {
                    newBlue = 1;
                }

                Color color = new Color(newRed, newGreen, newBlue, 1.0);

                result[i][j] = color;
            }
        }

        return result;
    }

    public Color[][] applyGreyscale(Color[][] pixels) {
        Color[][] result = new Color[pixels.length][pixels[0].length];

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                double red = pixels[i][j].getRed();
                double green = pixels[i][j].getGreen();
                double blue = pixels[i][j].getBlue();
                double newRed = (red + green + blue) / 3;

                if (newRed < 0) {
                    newRed = 0;
                } else if (newRed > 1) {
                    newRed = 1;
                }

                double newGreen = (red + green + blue) / 3;

                if (newGreen < 0) {
                    newGreen = 0;
                } else if (newGreen > 1) {
                    newGreen = 1;
                }

                double newBlue = (red + green + blue) / 3;

                if (newBlue < 0) {
                    newBlue = 0;
                } else if (newBlue > 1) {
                    newBlue = 1;
                }

                Color color = new Color(newRed, newGreen, newBlue, 1.0);

                result[i][j] = color;
            }
        }
        return result;
    }

    public void filterImage(String filterType) {

        Color[][] pixels = getPixelDataExtended();

        float[][] filter = createFilter(filterType);

        Color[][] filteredImage = applyFilter(pixels, filter);

        WritableImage wimg = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());

        PixelWriter pw = wimg.getPixelWriter();

        for (int i = 0; i < wimg.getHeight(); i++) {
            for (int j = 0; j < wimg.getWidth(); j++) {
                pw.setColor(i, j, filteredImage[i][j]);
            }
        }

        File newFile = new File("filtered_" + filterType + "_" + this.currentFilename);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wimg, null), "png", newFile);
        } catch (Exception s) {
        }

        initialiseVBox(false);

        image = wimg;
        imgv = new ImageView(wimg);
        vbox.getChildren().add(imgv);
    }

    private void sepia() {

        Color[][] pixels = getPixelData();

        Color[][] newPixels = applySepia(pixels);

        WritableImage wimg = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());

        PixelWriter pw = wimg.getPixelWriter();

        for (int i = 0; i < wimg.getHeight(); i++) {
            for (int j = 0; j < wimg.getWidth(); j++) {
                pw.setColor(i, j, newPixels[i][j]);
            }
        }

        File newFile = new File("filtered_SEPIA_" + this.currentFilename);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wimg, null), "png", newFile);
        } catch (Exception s) {
        }

        initialiseVBox(false);

        image = wimg;
        imgv = new ImageView(wimg);
        vbox.getChildren().add(imgv);
    }

    private void greyscale() {
        Color[][] pixels = getPixelData();

        Color[][] newPixels = applyGreyscale(pixels);

        WritableImage wimg = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());

        PixelWriter pw = wimg.getPixelWriter();

        for (int i = 0; i < wimg.getHeight(); i++) {
            for (int j = 0; j < wimg.getWidth(); j++) {
                pw.setColor(i, j, newPixels[i][j]);
            }
        }

        File newFile = new File("filtered_GREYSCALE_" + this.currentFilename);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wimg, null), "png", newFile);
        } catch (Exception s) {
        }

        initialiseVBox(false);

        image = wimg;
        imgv = new ImageView(wimg);
        vbox.getChildren().add(imgv);

    }

    private Color[][] getPixelData() {
        PixelReader pr = image.getPixelReader();
        Color[][] pixels = new Color[(int) image.getWidth()][(int) image.getHeight()];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                pixels[i][j] = pr.getColor(i, j);
            }
        }

        return pixels;
    }

    private Color[][] getPixelDataExtended() {
        PixelReader pr = image.getPixelReader();
        Color[][] pixels = new Color[(int) image.getWidth() + 2][(int) image.getHeight() + 2];

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels.length; j++) {
                pixels[i][j] = new Color(1.0, 1.0, 1.0, 1.0);
            }
        }

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                pixels[i + 1][j + 1] = pr.getColor(i, j);
            }
        }

        return pixels;
    }

    private void initialiseStage(Stage stage) {
        stage.setTitle("Image Processor");
        scene = new Scene(new VBox(), STAGE_WIDTH, STAGE_HEIGHT);
        scene.setFill(Color.OLDLACE);
    }

    @Override
    public void start(Stage stage) {

        initialiseStage(stage);

        initialiseVBox(true);

        createMenuItems();

        enableMenuItem("open");

        createStage(stage);
    }

    private void createStage(Stage stage) {

        Menu menuFile = new Menu("File");

        MenuItem open = getMenuItem("open");

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Image File");
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    enableAllMenuItems();
                    disableMenuItem("open");
                    openFile(file);
                }
            }
        });

        menuFile.getItems().add(open);

        MenuItem close = getMenuItem("close");

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                disableMenuItem("close");
                closeFile();
            }
        });

        menuFile.getItems().add(close);

        Menu menuTools = new Menu("Tools");

        MenuItem greyscale = getMenuItem("greyscale");

        greyscale.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                greyscale();
            }
        });

        menuTools.getItems().add(greyscale);

        MenuItem blur = getMenuItem("blur");

        blur.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                filterImage("BLUR");
            }
        });

        menuTools.getItems().add(blur);

        MenuItem sharpen = getMenuItem("sharpen");

        sharpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                filterImage("SHARPEN");
            }
        });

        menuTools.getItems().add(sharpen);

        MenuItem edge = getMenuItem("edge");

        edge.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                filterImage("EDGE");
            }
        });

        menuTools.getItems().add(edge);

        MenuItem sepia = getMenuItem("sepia");

        sepia.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                sepia();
            }
        });

        menuTools.getItems().add(sepia);

        MenuItem emboss = getMenuItem("emboss");

        emboss.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                filterImage("EMBOSS");
            }
        });

        menuTools.getItems().add(emboss);

        MenuItem identity = getMenuItem("identity");

        identity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                filterImage("IDENTITY");
            }
        });

        menuTools.getItems().add(identity);

        MenuItem reset = getMenuItem("reset");

        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                reset();
            }
        });

        menuTools.getItems().add(reset);

        MenuBar menuBar = new MenuBar();

        menuBar.getMenus().addAll(menuFile, menuTools);

        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, vbox);

        stage.setScene(scene);

        stage.show();
    }

    protected void reset() {
        initialiseVBox(false);
        openFile(new File(currentFilename));
    }

    private void initialiseVBox(boolean create) {

        final int LEFT = 10;
        final int RIGHT = 10;
        final int TOP = 10;
        final int BOTTOM = 10;


        if (create) {
            vbox = new VBox();
        }
        vbox.getChildren().clear();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(LEFT,TOP,RIGHT,BOTTOM));
    }

    private void createMenuItems() {
        menuItems = new ArrayList<MenuItem>();
        menuItems.add(new MenuItem("Open"));
        menuItems.add(new MenuItem("Close"));
        menuItems.add(new MenuItem("Greyscale"));
        menuItems.add(new MenuItem("Blur"));
        menuItems.add(new MenuItem("Sharpen"));
        menuItems.add(new MenuItem("Sepia"));
        menuItems.add(new MenuItem("Emboss"));
        menuItems.add(new MenuItem("Edge"));
        menuItems.add(new MenuItem("Identity"));
        menuItems.add(new MenuItem("Reset"));
        disableAllMenuItems();
    }

    private void disableAllMenuItems() {
        for (MenuItem m: menuItems) {
            m.setDisable(true);
        }
    }

    private void enableAllMenuItems() {
        for (MenuItem m: menuItems) {
            m.setDisable(false);
        }
    }

    private void disableMenuItem(String item) {
        for (MenuItem m: menuItems) {
            if (m.getText().equalsIgnoreCase(item)) {
                m.setDisable(true);
            }
        }
    }

    private void enableMenuItem(String item) {
        for (MenuItem m: menuItems) {
            if (m.getText().equalsIgnoreCase(item)) {
                m.setDisable(false);
            }
        }
    }

    private MenuItem getMenuItem(String name) {
        for (MenuItem m: menuItems) {
            if (m.getText().equalsIgnoreCase(name)) {
                return m;
            }
        }

        return null;
    }

    private void closeFile() {
        enableMenuItem("open");
        initialiseVBox(false);
    }

    private void openFile(File file) {

        image = new Image("file:" + file.getPath());

        if (image.getWidth() != image.getHeight()) {
            Alert alert = new Alert(AlertType.ERROR, "Image is not square.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        imgv = new ImageView();
        imgv.setImage(image);
        vbox.getChildren().add(imgv);
        currentFilename = file.getName();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
