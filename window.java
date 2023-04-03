import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

public class window {
    public window() {
        init();
        x = frame.getLocation().x;
        y = frame.getLocation().y;
    }
    public window(int width, int height) {
        this.width = width;
        this.height = height;
        update_size();
        init();
    }
    public window(String title) {
        this.title = title;
        frame.setTitle(title);
        init();
    }
    public window(String title, int width, int height) {
        this.width = width;
        this.height = height;
        update_size();
        this.title = title;
        frame.setTitle(title);
        init();
    }
    public window(String title, int width, int height, boolean visible) {
        this.width = width;
        this.height = height;
        update_size();
        this.title = title;
        frame.setTitle(title);
        this.visible = visible;
        frame.setVisible(visible);
        init();
    }

    public void init() {

    }
    public class interval {
        public boolean running = true;

        public int interval = 0;
        public Runnable target = null;

        public static final BiConsumer<Integer, Runnable> WAIT_METHOD = (interval, target) -> {
            target.run();
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        public static final BiConsumer<Integer, Runnable> DIFFERENCE_WAIT_METHOD = (interval, target) -> {
            long start_time = System.currentTimeMillis();
            target.run();
            try {
                Thread.sleep(Math.max(0, interval + start_time - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        public BiConsumer<Integer, Runnable> sleep_method = WAIT_METHOD;

        public Thread th = new Thread(() -> {
            while (running) {
                sleep_method.accept(interval, target);
            }
        });

        public interval(int interval, Runnable target) {
            this.interval = interval;
            this.target = target;
            th.start();
        }
        public interval(int interval, Runnable target, BiConsumer<Integer, Runnable> type) {
            sleep_method = type;
            this.interval = interval;
            this.target = target;
            th.start();
        }
    }
    public interval set_interval(int interval, Runnable target) {
        return new interval(interval, target);
    }
    public interval set_interval(int interval, Runnable target, BiConsumer<Integer, Runnable> type) {
        return new interval(interval, target, type);
    }
    private JFrame frame = new JFrame() {
        {
            addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent componentEvent) { }

                @Override
                public void componentMoved(ComponentEvent componentEvent) {
                    x = componentEvent.getComponent().getX();
                    y = componentEvent.getComponent().getY();
                    for (int n = 0;n != move_listeners.size();n++) {
                        move_listeners.get(n).accept(new move_event() {{
                            x = componentEvent.getComponent().getX();
                            y = componentEvent.getComponent().getY();
                        }});
                    }
                }

                @Override
                public void componentShown(ComponentEvent componentEvent) {}

                @Override
                public void componentHidden(ComponentEvent componentEvent) { }
            });
            add(new JPanel() {
                {
                    addMouseMotionListener(new MouseMotionListener() {
                        @Override
                        public void mouseDragged(MouseEvent mouseEvent) {
                            is_dragge = true;
                            mouse_x = mouseEvent.getX();
                            mouse_y = mouseEvent.getY();
                            for (int n = 0;n != motion_listeners.size();n++) {
                                motion_listeners.get(n).accept(new motion_event() {{
                                    is_dragge = true;
                                    position_x = mouseEvent.getX();
                                    position_y = mouseEvent.getY();
                                }});
                            }
                        }

                        @Override
                        public void mouseMoved(MouseEvent mouseEvent) {
                            is_dragge = false;
                            mouse_x = mouseEvent.getX();
                            mouse_y = mouseEvent.getY();
                            for (int n = 0;n != motion_listeners.size();n++) {
                                motion_listeners.get(n).accept(new motion_event() {{
                                    position_x = mouseEvent.getX();
                                    position_y = mouseEvent.getY();
                                }});
                            }
                        }
                    });
                    addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent mouseEvent) {}

                        @Override
                        public void mousePressed(MouseEvent mouseEvent) {
                            switch (mouseEvent.getButton() - 1) {
                                case click_event.RIGHT_BUTTON:
                                    flush_data.is_right_press = true;
                                    is_right_press = true;
                                    break;
                                case click_event.MIDDLE_BUTTON:
                                    flush_data.is_middle_press = true;
                                    is_middle_press = true;
                                    break;
                                case click_event.LEFT_BUTTON:
                                    flush_data.is_left_press = true;
                                    is_left_press = true;
                                    break;
                            }
                            for (int n = 0;n != click_listeners.size();n++) {
                                click_listeners.get(n).accept(new click_event() {{
                                    button = mouseEvent.getButton() - 1;
                                    is_press = true;
                                    position_x = mouseEvent.getX();
                                    position_y = mouseEvent.getY();
                                }});
                            }
                        }

                        @Override
                        public void mouseReleased(MouseEvent mouseEvent) {
                            switch (mouseEvent.getButton() - 1) {
                                case click_event.RIGHT_BUTTON:
                                    is_right_press = false;
                                    break;
                                case click_event.MIDDLE_BUTTON:
                                    is_middle_press = false;
                                    break;
                                case click_event.LEFT_BUTTON:
                                    is_left_press = false;
                                    break;
                            }
                            for (int n = 0;n != click_listeners.size();n++) {
                                click_listeners.get(n).accept(new click_event() {{
                                    button = mouseEvent.getButton() - 1;
                                    is_release = true;
                                    position_x = mouseEvent.getX();
                                    position_y = mouseEvent.getY();
                                }});
                            }
                        }

                        @Override
                        public void mouseEntered(MouseEvent mouseEvent) {
                            is_enter = true;
                            is_exit = false;
                            for (int n = 0;n != enter_listeners.size();n++) {
                                enter_listeners.get(n).accept(new enter_event() {{
                                    is_enter = true;
                                }});
                            }
                        }

                        @Override
                        public void mouseExited(MouseEvent mouseEvent) {
                            is_enter = false;
                            is_exit = true;
                            for (int n = 0;n != enter_listeners.size();n++) {
                                enter_listeners.get(n).accept(new enter_event() {{
                                    is_enter = false;
                                }});
                            }
                        }
                    });
                    addMouseWheelListener(new MouseWheelListener() {
                        @Override
                        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                            for (int n = 0;n != wheel_listeners.size();n++) {
                                wheel_listeners.get(n).accept(new wheel_event() {{
                                    scroll = mouseWheelEvent.getWheelRotation();
                                    if (mouseWheelEvent.isShiftDown()) {
                                        is_vertical = true;
                                    } else {
                                        is_horizontal = true;
                                    }
                                }});
                            }
                        }
                    });
                    addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent windowEvent) { }

                        @Override
                        public void windowClosing(WindowEvent windowEvent) {
                            if (on_close != null)
                                on_close.run();
                        }

                        @Override
                        public void windowClosed(WindowEvent windowEvent) { }

                        @Override
                        public void windowIconified(WindowEvent windowEvent) { }

                        @Override
                        public void windowDeiconified(WindowEvent windowEvent) { }

                        @Override
                        public void windowActivated(WindowEvent windowEvent) {
                            for (int n = 0;n != active_listeners.size();n++) {
                                active_listeners.get(n).accept(new active_event() {{
                                    is_active = true;
                                }});
                            }
                        }

                        @Override
                        public void windowDeactivated(WindowEvent windowEvent) {
                            for (int n = 0;n != active_listeners.size();n++) {
                                active_listeners.get(n).accept(new active_event() {{
                                    is_deactive = true;
                                }});
                            }
                        }
                    });
                    addComponentListener(new ComponentListener() {
                        @Override
                        public void componentResized(ComponentEvent componentEvent) {
                            width = componentEvent.getComponent().getWidth();
                            height = componentEvent.getComponent().getHeight();
                            for (int n = 0;n != resize_listeners.size();n++) {
                                resize_listeners.get(n).accept(new resize_event() {{
                                    width = componentEvent.getComponent().getWidth();
                                    height = componentEvent.getComponent().getHeight();
                                }});
                            }
                        }

                        @Override
                        public void componentMoved(ComponentEvent componentEvent) {}

                        @Override
                        public void componentShown(ComponentEvent componentEvent) { }

                        @Override
                        public void componentHidden(ComponentEvent componentEvent) { }
                    });
                    setFocusable(true);
                    addKeyListener(new KeyListener() {
                        @Override
                        public void keyTyped(KeyEvent keyEvent) { }

                        @Override
                        public void keyPressed(KeyEvent keyEvent) {
                            if (!is_press(keyEvent.getKeyCode())) {
                                press_keys.add(keyEvent.getKeyCode());
                                flush_data.press_keys.add(keyEvent.getKeyCode());
                            }
                            for (int n = 0;n != keybind_listeners.size();n++) {
                                keybind_listeners.get(n).accept(new keybind_event() {{
                                    key_code = keyEvent.getKeyCode();
                                    key_char = keyEvent.getKeyChar();
                                    is_press = true;
                                }});
                            }
                        }

                        @Override
                        public void keyReleased(KeyEvent keyEvent) {
                            if (is_press(keyEvent.getKeyCode())) {
                                press_keys.remove(press_keys.indexOf(keyEvent.getKeyCode()));
                                flush_data.press_keys.remove(flush_data.press_keys.indexOf(keyEvent.getKeyCode()));
                            }
                            for (int n = 0;n != keybind_listeners.size();n++) {
                                keybind_listeners.get(n).accept(new keybind_event() {{
                                    key_code = keyEvent.getKeyCode();
                                    key_char = keyEvent.getKeyChar();
                                    is_release = true;
                                }});
                            }
                        }
                    });
                }
                @Override
                public void paintComponent(Graphics g) {
                    BufferedImage window_contents = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                    draw(window_contents, window_contents.createGraphics());
                    g.drawImage(window_contents, 0, 0, null);
                }
            });
        }
    };

    private boolean visible = false;
    private boolean resizable = false;
    private int width = 0;
    private int height = 0;
    private String title = "";
    private int x = 0;
    private int y = 0;

    public void set_title(String title) {
        this.title = title;
        frame.setTitle(title);
    }

    public void set_visible(boolean visible) {
        this.visible = visible;
        frame.setVisible(visible);
    }
    public void set_resizable(boolean resizable) {
        this.resizable = resizable;
        frame.setResizable(resizable);
    }
    public void show() {
        visible = true;
        frame.setVisible(true);
    }
    public void hide() {
        visible = false;
        frame.setVisible(false);
    }

    public void set_width(int width) {
        this.width = width;
        update_size();
    }
    public void set_height(int height) {
        this.height = height;
        update_size();
    }
    public void set_size(int width, int height) {
        this.width = width;
        this.height = height;
        update_size();
    }
    public void set_always_on_top(boolean b) {
        frame.setAlwaysOnTop(b);
    }
    private void update_size() {
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.pack();
    }

    public void set_location(int x, int y) {
        this.x = x;
        this.y = y;
        frame.setLocation(x, y);
    }
    public void set_location_x(int x) {
        this.x = x;
        frame.setLocation(x, y);
    }
    public void set_location_y(int y) {
        this.y = y;
        frame.setLocation(x, y);
    }
    public void set_location_center() {
        frame.setLocationRelativeTo(null);
        x = frame.getLocation().x;
        y = frame.getLocation().y;
    }

    public boolean get_visible() { return visible; }
    public int get_width() { return width; }
    public int get_height() { return height; }
    public String get_title() { return new String(title); }
    public int get_x() { return x; }
    public int get_y() { return y; }
    public boolean get_resizable() { return resizable; }


    public void repaint() {
        frame.repaint();
    }
    public void draw(BufferedImage window, Graphics2D g) { }

    boolean is_dragge = false;
    int mouse_x = 0;
    int mouse_y = 0;

    boolean is_right_press = false;
    boolean is_middle_press = false;
    boolean is_left_press = false;

    public boolean is_enter = false;
    public boolean is_exit = false;

    private List<Integer> press_keys = new ArrayList<>();

    public boolean is_press(int key_code) {
        return press_keys.contains(key_code);
    }

    public class flush_memory {
        public boolean is_right_press = false;
        public boolean is_middle_press = false;
        public boolean is_left_press = false;

        public int wheel_vector = 0;
        public int wheel_trun_x = 0;
        public int wheel_trun_y = 0;

        public List<Integer> press_keys = new ArrayList<>();

        public boolean is_press(int key_code) {
            return press_keys.contains(key_code);
        }
    }
    public void flush() {
        flush_data.is_right_press = false;
        flush_data.is_middle_press = false;
        flush_data.is_left_press = false;

        flush_data.wheel_vector = 0;
        flush_data.wheel_trun_x = 0;
        flush_data.wheel_trun_y = 0;

        press_keys = new ArrayList<>();
    }
    public flush_memory flush_data = new flush_memory();

    private List<Consumer<motion_event>> motion_listeners = new ArrayList<>();
    public class motion_event {
        public boolean is_dragge = false;
        public int position_x = 0;
        public int position_y = 0;

        @Override public String toString() {
            return "[position_x: " + position_x + ", position_y: " + position_y + (is_dragge ? ", #is_dragge" : "") + "]";
        }
    }
    public void add_motion_listener(Consumer<motion_event> listener) {
        motion_listeners.add(listener);
    }
    private List<Consumer<click_event>> click_listeners = new ArrayList<>();
    public class click_event {
        public static final int RIGHT_BUTTON = 0;
        public static final int MIDDLE_BUTTON = 1;
        public static final int LEFT_BUTTON = 2;

        public int button = 0;
        public boolean is_press = false;
        public boolean is_release = false;
        public int position_x = 0;
        public int position_y = 0;

        @Override public String toString() {
            return "[button: " + new ArrayList<>(Arrays.asList("RIGHT", "MIDDLE", "LEFT")).get(button) + ", status: " + (is_press ? "press" : "release") + ", position_x: " + this.position_x + ", position_y: " + position_y + "]";
        }
    }
    public void add_click_listener(Consumer<click_event> listener) {
        click_listeners.add(listener);
    }
    private List<Consumer<wheel_event>> wheel_listeners = new ArrayList<>();
    public class wheel_event {
        public int scroll = 0;

        public boolean is_horizontal = false;
        public boolean is_vertical = false;

        @Override public String toString() {
            return "[scroll: " + scroll + ", dirction: " + (is_horizontal ? "HORIZONTAL" : "VERTICAL") + "]";
        }
    }
    public void add_wheel_listener(Consumer<wheel_event> listener) {
        wheel_listeners.add(listener);
    }
    private List<Consumer<enter_event>> enter_listeners = new ArrayList<>();
    public class enter_event {
        public boolean is_enter = false;
        public boolean is_exit = false;

        @Override public String toString() {
            return "[status: " + (is_enter ? "enter" : "exit") + "]";
        }
    }
    public void add_enter_listener(Consumer<enter_event> listener) {
        enter_listeners.add(listener);
    }
    private List<Consumer<resize_event>> resize_listeners = new ArrayList<>();
    public class resize_event {
        public int width = 0;
        public int height = 0;

        @Override public String toString() {
            return "[width: " + width + ", height: " + height + "]";
        }
    }
    public void add_resize_listener(Consumer<resize_event> listener) {
        resize_listeners.add(listener);
    }
    private List<Consumer<move_event>> move_listeners = new ArrayList<>();
    public class move_event {
        public int x = 0;
        public int y = 0;

        @Override public String toString() {
            return "[x: " + x + ", y: " + y + "]";
        }
    }
    public void add_move_listener(Consumer<move_event> listener) {
        move_listeners.add(listener);
    }
    private List<Consumer<active_event>> active_listeners = new ArrayList<>();
    public class active_event {
        public boolean is_active = false;
        public boolean is_deactive = false;

        @Override public String toString() {
            return "[status: " + (is_active ? "ACTIVE" : "DEACTIVE") + "]";
        }
    }
    public void add_active_listener(Consumer<active_event> listener) {
        active_listeners.add(listener);
    }
    private List<Consumer<keybind_event>> keybind_listeners = new ArrayList<>();
    public class keybind_event {
        public int key_code = 0;
        public char key_char = 0;
        public boolean is_press = false;
        public boolean is_release = false;

        @Override public String toString() {
            return "[code: " + key_code + ", char: " + key_char + ", status: " + (is_press ? "press" : "release") + "]";
        }
    }
    public void add_keybind_listener(Consumer<keybind_event> listener) {
        keybind_listeners.add(listener);
    }
    private Runnable on_close = null;
    public void set_close_listener(Runnable listener) {
        on_close = listener;
    }

    public final Runnable EXIT_ON_CLOSE = new Runnable() {
        @Override
        public void run() {
            System.exit(0);
        }
    };
    public final Runnable HIDE_ON_CLOSE = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    public static void messagebox(String title, String value) {
        JOptionPane.showMessageDialog(null, value, title, 0);
    }
}
