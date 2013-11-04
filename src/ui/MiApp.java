package ui;

import java.awt.Desktop;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import client.Mi;
import client.MiClientException;

public class MiApp {

    protected Shell shell;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            MiApp window = new MiApp();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {
                int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
                MessageBox messageBox = new MessageBox(shell, style);
                messageBox.setText("信息");
                messageBox.setMessage("确认关闭?");
                if (messageBox.open() == SWT.YES) {
                    running = false;
                    executorService.shutdownNow();
                    System.exit(0); // .........
                } else {
                    event.doit = false;
                }
            }
        });

        shell.setSize(797, 523);
        shell.setText("SWT Application");

        Label label1 = new Label(shell, SWT.NONE);
        label1.setBounds(21, 29, 50, 17);
        label1.setText("用户名：");

        userText = new Text(shell, SWT.BORDER);
        userText.setText("13341052808");
        userText.setBounds(82, 26, 150, 23);

        Label label2 = new Label(shell, SWT.NONE);
        label2.setBounds(21, 75, 50, 17);
        label2.setText("密  码：");

        passText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
        passText.setText("baocm000");
        passText.setBounds(82, 72, 150, 23);

        final StyledText styledText = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        styledText.setEditable(false);
        styledText.setBounds(310, 10, 461, 465);
        styledText.setSelection(styledText.getCharCount());

        final Label label = new Label(shell, SWT.NONE);
        label.setText("请先登录！");
        label.setBounds(31, 133, 61, 17);

        Button button_1 = new Button(shell, SWT.NONE);
        button_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (mi.isLogin()) {
                    styledText.append("已经登录！\n");
                    return;
                }
                try {
                    boolean login = mi.login(userText.getText(), passText.getText());
                    if (login) {
                        styledText.append("登录成功！\n");
                        label.setText("已经登录！");
                    } else {
                        styledText.append("登录失败！\n");
                    }
                    styledText.append(mi.getCookieLines());
                } catch (MiClientException e1) {
                    styledText.append(e1.toString());
                }
            }
        });
        button_1.setBounds(152, 123, 80, 27);
        button_1.setText("登录");

        // final Button imageCodeButton = new Button(shell, SWT.NONE);
        // imageCodeButton.setEnabled(false);
        // imageCodeButton.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // Image oldImage = imageCodeLabel.getImage();
        // if (oldImage != null) {
        // oldImage.dispose();
        // }
        // InputStream input = null;
        // try {
        // input = mi.getCodeImageInputStream();
        // } catch (MiClientException e1) {
        // imageCodeLabel.setText("验证码获取异常！");
        // }
        // Image image = new Image(Display.getCurrent(), input);
        // imageCodeLabel.setImage(image);
        // }
        // });
        // imageCodeButton.setBounds(113, 246, 80, 25);
        // imageCodeButton.setText("刷新验证码");

        Label label4 = new Label(shell, SWT.SEPARATOR);
        label4.setBounds(272, 10, 28, 465);

        final Button button = new Button(shell, SWT.NONE);
        button.setBounds(38, 219, 84, 32);
        button.setText("开始请求排队");

        final Button stopQueueButton = new Button(shell, SWT.NONE);

        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                running = true;
                int tcount = 4;
                for (int i = 0; i < tcount; i++) {
                    final int fi = i;
                    final Runnable paiduiRunnable = new Runnable() {
                        public void run() {
                            while (true) {
                                if (!running) {
                                    break;
                                }
                                final Map<String, String> hdurlMap = mi.paidui();
                                // final String hdurl = null;
                                if (hdurlMap != null && StringUtils.isNotBlank(hdurlMap.get("miphonehdurl"))) {
                                    Display.getDefault().syncExec(new Runnable() {
                                        public void run() {
                                            hdurlText.setText(StringUtils.defaultString(hdurlMap.get("miphonehdurl"),
                                                    ""));
                                            hdurlText2.setText(StringUtils.defaultString(hdurlMap.get("miphonehdurl"),
                                                    ""));
                                            styledText.append(fi + " - 购买地址获取成功！！！！！\n");
                                            styledText.setSelection(styledText.getCharCount());
                                            button.setEnabled(true);
                                        }
                                    });
                                    break;
                                }

                                Display.getDefault().syncExec(new Runnable() {
                                    public void run() {
                                        styledText.append(fi + " - 购买地址获取失败.  ");
                                        styledText.append(hdurlMap.get("msg") + "\n");
                                        styledText.setSelection(styledText.getCharCount());
                                    }
                                });

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            running = false;
                        }
                    };
                    executorService.execute(paiduiRunnable);
                }

                styledText.append(tcount + "个线程开始请求排队...\n");
                button.setEnabled(false);
                stopQueueButton.setEnabled(true);
            }
        });

        stopQueueButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                running = false;
                button.setEnabled(true);
                stopQueueButton.setEnabled(false);
            }
        });
        stopQueueButton.setBounds(152, 219, 80, 32);
        stopQueueButton.setText("停止排队");
        stopQueueButton.setEnabled(false);

        hdurlText = new Text(shell, SWT.BORDER);
        hdurlText.setBounds(10, 319, 262, 23);

        Label lblurl = new Label(shell, SWT.NONE);
        lblurl.setBounds(10, 285, 61, 17);
        lblurl.setText("购买URL：");

        Button button_2 = new Button(shell, SWT.NONE);
        button_2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!Desktop.isDesktopSupported()) {
                    // 测试当前平台是否支持此类
                    JOptionPane.showMessageDialog(null, "浏览器设置不支持");
                    return;
                }
                if (StringUtils.isNotBlank(hdurlText.getText())) {
                    // 用来打开系统默认浏览器浏览指定的URL
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        // 创建URI统一资源标识符
                        URI uri = new URI(hdurlText.getText());
                        // 使用默认浏览器打开超链接
                        desktop.browse(uri);
                    } catch (Exception ex) {
                    }
                }
                if (StringUtils.isNotBlank(hdurlText2.getText())) {
                    // 用来打开系统默认浏览器浏览指定的URL
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        // 创建URI统一资源标识符
                        URI uri = new URI(hdurlText2.getText());
                        // 使用默认浏览器打开超链接
                        desktop.browse(uri);
                    } catch (Exception ex) {
                    }
                }
            }
        });
        button_2.setBounds(192, 408, 80, 27);
        button_2.setText("打开浏览器");

        hdurlText2 = new Text(shell, SWT.BORDER);
        hdurlText2.setBounds(10, 364, 262, 23);

        Button btnNewButton = new Button(shell, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                styledText.append(mi.getCookieLines());
                styledText.setSelection(styledText.getCharCount());
            }
        });
        btnNewButton.setBounds(23, 448, 80, 27);
        btnNewButton.setText("查看cookie");

        try {
            mi = new Mi();
            mi.visitIndex();
            styledText.append(mi.getCookieLines());
        } catch (Throwable miTh) {
            styledText.append(miTh.toString());
        }
    }

    private Text userText;
    private Text passText;

    private Mi mi;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Text hdurlText;
    private Text hdurlText2;
    private volatile boolean running;
}
