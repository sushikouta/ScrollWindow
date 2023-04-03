import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public class canvas extends window {
        public static final int default_width = 600;
        public static final int default_height = 600;

        public canvas() {
            super("Image Editor - canvas", default_width, default_height, true);
            /* view_image = new BufferedImage(1000, 1000, BufferedImage.TYPE_4BYTE_ABGR) {{
                Graphics2D g = createGraphics();
                g.setColor(Color.LIGHT_GRAY);
                for (int x = 0;x != getWidth() / 50;x++) {
                    for (int y = 0;y != getHeight() / 50;y++) {
                        g.fillOval(x * 50 + 20, y * 50 + 20, 10, 10);
                    }
                }
            }}; */

            try {
                view_image = ImageIO.read(new File("./cycle.jpeg"));
                view_range_width = view_image.getWidth() * 2;
                view_range_height = view_image.getHeight() * 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override public void init() {
            set_close_listener(EXIT_ON_CLOSE);
            add_click_listener(this::on_mouse_click);
            add_resize_listener(this::on_resize);
            add_wheel_listener(this::on_wheel);
            new Thread(() -> {
                while (true) {
                    repaint();
                }
            }) {{
                start();
            }};
        }

        @Override public void draw(BufferedImage window, Graphics2D g) {
            g.fillRect(0, 0, get_width() - 20, get_height() - 20);
            g.setColor(Color.RED);
            g.drawImage(
                view_image, 
                - (int) ((view_range_width - get_width() + 20) * (double) scroll_x / (get_width() - (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20)) - 20)),
                - (int) ((view_range_height - get_height() + 20) * (double) scroll_y / (get_height() - (int) ((double) (get_height() - 20) / view_range_width * (get_height() - 20)) - 20)),
                null
            );
            g.setColor(new Color(0xf0f0f0));
            g.fillRect(get_width() - 20, 0, 20, get_height() - 20);
            g.fillRect(0, get_height() - 20, get_width() - 20, 20);
            g.setColor(new Color(0x616161));
            if (view_range_width > get_width() - 20) {
                g.fillRect(scroll_x, get_height() - 20, (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20)), 20);
                if (hover_id == 1) {
                    scroll_x += mouse_x - scroll_X;
                    if (scroll_x < 0) {
                        scroll_x = 0;
                    } else if (scroll_x + (double) (get_width() - 20) / view_range_width * (get_width() - 20) > get_width() - 20) {
                        scroll_x = get_width() - 20 - (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20));
                    }
                    scroll_X = mouse_x;
                }
            }
            if (view_range_height > get_height() - 20) {
                g.fillRect(get_width() - 20, scroll_y, 20, (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20)));
                if (hover_id == 2) {
                    scroll_y += mouse_y - scroll_Y;
                    if (scroll_y < 0) {
                        scroll_y = 0;
                    } else if (scroll_y + (double) (get_height() - 20) / view_range_height * (get_height() - 20) > get_height() - 20) {
                        scroll_y = get_height() - 20 - (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20));
                    }
                    scroll_Y = mouse_y;
                }
            }
            g.setColor(Color.GRAY);
            g.fillRect(get_width() - 20, get_height() - 20, 20, 20);
            g.setColor(Color.RED);
        }

        public int hover_id = 0;

        public int before_width = default_width;
        public int before_height = default_height;
        public int scroll_x = 0;
        public int scroll_y = 0;
        public int scroll_X = 0;
        public int scroll_Y = 0;

        public BufferedImage view_image = null;
        public int view_range_width = 1000;
        public int view_range_height = 1000;

        public void on_mouse_click(window.click_event event) {
            if (event.is_release) {
                hover_id = 0;
                return;
            }
            if (scroll_x <= mouse_x && mouse_x <= scroll_x + (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20)) && mouse_y > get_height() - 20) {
                hover_id = 1;
                scroll_X = mouse_x;
            } else if (scroll_y <= mouse_y && mouse_y <= scroll_y + (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20)) && mouse_x > get_width() - 20) {
                hover_id = 2;
                scroll_Y = mouse_y;
            }
        }

        public void on_resize(window.resize_event event) {
            if (view_range_width > get_width() - 20) {
                scroll_x = (int) ((double) scroll_x / (before_width - 20 - (int) ((double) (before_width - 20) / view_range_width * (before_width - 20))) * (event.width - 20 - (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20))));
            } else {
                scroll_x = 0;
            }
            if (view_range_height > get_height() - 20) {
                scroll_y = (int) ((double) scroll_y / (before_height - 20 - (int) ((double) (before_height - 20) / view_range_height * (before_height - 20))) * (event.height - 20 - (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20))));
            } else {
                scroll_y = 0;
            }
            before_width = event.width;
            before_height = event.height;
        }

        public void on_wheel(window.wheel_event event) {
            if (event.is_horizontal) {
                scroll_y += event.scroll * 2;
                if (scroll_y < 0) {
                    scroll_y = 0;
                } else if (scroll_y + (double) (get_height() - 20) / view_range_height * (get_height() - 20) > get_height() - 20) {
                    scroll_y = get_height() - 20 - (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20));
                }
            } else {
                scroll_x += event.scroll * 2;
                if (scroll_x < 0) {
                    scroll_x = 0;
                } else if (scroll_x + (double) (get_width() - 20) / view_range_width * (get_width() - 20) > get_width() - 20) {
                    scroll_x = get_width() - 20 - (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20));
                }
            }
        }
    };

    canvas canvas_window = new canvas();
}

/*
 * import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public class canvas extends window {
        public static final int default_width = 600;
        public static final int default_height = 600;

        public canvas() {
            super("Image Editor - canvas", default_width, default_height, true);

            try {
                view_image = ImageIO.read(new File("./cycle.jpeg"));
                view_range_width = view_image.getWidth();
                view_range_height = view_image.getHeight();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override public void init() {
            set_close_listener(EXIT_ON_CLOSE);
            add_click_listener(this::on_mouse_click);
            add_resize_listener(this::on_resize);
            new Thread(() -> {
                while (true) {
                    repaint();
                }
            }) {{
                start();
            }};
        }

        @Override public void draw(BufferedImage window, Graphics2D g) {
            g.fillRect(0, 0, get_width() - 20, get_height() - 20);
            g.setColor(Color.RED);
            g.drawImage(
                view_image, 
                - (int) ((view_range_width - get_width() + 20) * (double) scroll_x / (get_width() - (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20)) - 20)),
                - (int) ((view_range_height - get_height() + 20) * (double) scroll_y / (get_height() - (int) ((double) (get_height() - 20) / view_range_width * (get_height() - 20)) - 20)),
                null
            );
            g.setColor(new Color(0xf0f0f0));
            g.fillRect(get_width() - 20, 0, 20, get_height() - 20);
            g.fillRect(0, get_height() - 20, get_width() - 20, 20);
            g.setColor(new Color(0x616161));
            if (view_range_width > get_width() - 20) {
                g.fillRect(scroll_x, get_height() - 20, (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20)), 20);
                if (hover_id == 1) {
                    scroll_x += mouse_x - scroll_X;
                    if (scroll_x < 0) {
                        scroll_x = 0;
                    } else if (scroll_x + (double) (get_width() - 20) / view_range_width * (get_width() - 20) > get_width() - 20) {
                        scroll_x = get_width() - 20 - (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20));
                    }
                    scroll_X = mouse_x;
                }
            }
            if (view_range_height > get_height() - 20) {
                g.fillRect(get_width() - 20, scroll_y, 20, (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20)));
                if (hover_id == 2) {
                    scroll_y += mouse_y - scroll_Y;
                    if (scroll_y < 0) {
                        scroll_y = 0;
                    } else if (scroll_y + (double) (get_height() - 20) / view_range_height * (get_height() - 20) > get_height() - 20) {
                        scroll_y = get_height() - 20 - (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20));
                    }
                    scroll_Y = mouse_y;
                }
            }
            g.setColor(Color.GRAY);
            g.fillRect(get_width() - 20, get_height() - 20, 20, 20);
            g.setColor(Color.RED);
        }

        public int hover_id = 0;

        public int before_width = default_width;
        public int before_height = default_height;
        public int scroll_x = 0;
        public int scroll_y = 0;
        public int scroll_X = 0;
        public int scroll_Y = 0;

        public BufferedImage view_image = null;
        public int view_range_width = 1000;
        public int view_range_height = 1000;

        public void on_mouse_click(window.click_event event) {
            if (event.is_release) {
                hover_id = 0;
                return;
            }
            if (scroll_x <= mouse_x && mouse_x <= scroll_x + (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20)) && mouse_y > get_height() - 20) {
                hover_id = 1;
                scroll_X = mouse_x;
            } else if (scroll_y <= mouse_y && mouse_y <= scroll_y + (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20)) && mouse_x > get_width() - 20) {
                hover_id = 2;
                scroll_Y = mouse_y;
            }
        }

        public void on_resize(window.resize_event event) {
            if (view_range_width > get_width() - 20) {
                scroll_x = (int) ((double) scroll_x / (before_width - 20 - (int) ((double) (before_width - 20) / view_range_width * (before_width - 20))) * (event.width - 20 - (int) ((double) (get_width() - 20) / view_range_width * (get_width() - 20))));
            } else {
                scroll_x = 0;
            }
            if (view_range_height > get_height() - 20) {
                scroll_y = (int) ((double) scroll_y / (before_height - 20 - (int) ((double) (before_height - 20) / view_range_height * (before_height - 20))) * (event.height - 20 - (int) ((double) (get_height() - 20) / view_range_height * (get_height() - 20))));
            } else {
                scroll_y = 0;
            }
            before_width = event.width;
            before_height = event.height;
        }
    };

    canvas canvas_window = new canvas();
}
 */