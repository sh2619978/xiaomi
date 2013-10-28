package ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent arg0) {
                running = false;
                executorService.shutdownNow();
                System.exit(0); // .........
            }
        });
        shell.setSize(797, 523);
        shell.setText("SWT Application");

        Label label1 = new Label(shell, SWT.NONE);
        label1.setBounds(21, 29, 50, 17);
        label1.setText("用户名：");

        userText = new Text(shell, SWT.BORDER);
        userText.setText("sh2619978@126.com");
        userText.setBounds(82, 26, 150, 23);

        Label label2 = new Label(shell, SWT.NONE);
        label2.setBounds(21, 75, 50, 17);
        label2.setText("密  码：");

        passText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
        passText.setText("ricebean2013");
        passText.setBounds(82, 72, 150, 23);

        final StyledText styledText = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        styledText.setEditable(false);
        styledText.setBounds(310, 10, 461, 465);
        styledText.setSelection(styledText.getCharCount());

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
                                final String hdurl = mi.paidui();
                                // final String hdurl = null;
                                if (StringUtils.isNotBlank(hdurl)) {
                                    Display.getDefault().syncExec(new Runnable() {
                                        public void run() {
                                            hdurlText.setText(hdurl);
                                            styledText.append(fi + " - 购买地址获取成功！！！！！\n");
                                            styledText.setSelection(styledText.getCharCount());
                                            button.setEnabled(true);
                                        }
                                    });
                                    break;
                                }

                                Display.getDefault().syncExec(new Runnable() {
                                    public void run() {
                                        styledText.append(fi + " - 购买地址获取失败.\n");
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
                            executorService.shutdownNow();
                        }
                    };
                    executorService.execute(paiduiRunnable);

                }

                styledText.append(tcount + "个线程开始请求排队...\n");
                button.setEnabled(false);
            }
        });
        button.setBounds(70, 219, 84, 32);
        button.setText("开始请求排队");

        hdurlText = new Text(shell, SWT.BORDER);
        hdurlText.setBounds(10, 319, 262, 23);

        Label lblurl = new Label(shell, SWT.NONE);
        lblurl.setBounds(10, 285, 61, 17);
        lblurl.setText("购买URL：");

        Button button_2 = new Button(shell, SWT.NONE);
        button_2.setBounds(192, 408, 80, 27);
        button_2.setText("打开浏览器");

        hdurlText2 = new Text(shell, SWT.BORDER);
        hdurlText2.setBounds(10, 364, 262, 23);

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
