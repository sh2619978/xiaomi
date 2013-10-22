package ui;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import client.Mi;
import client.MiClientException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

public class PiaoApp {

    protected Shell shell;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            PiaoApp window = new PiaoApp();
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
        shell.setSize(467, 464);
        shell.setText("SWT Application");
        
        Label label1 = new Label(shell, SWT.NONE);
        label1.setBounds(10, 25, 50, 17);
        label1.setText("用户名：");
        
        userText = new Text(shell, SWT.BORDER);
        userText.setBounds(71, 22, 150, 23);
        
        Label label2 = new Label(shell, SWT.NONE);
        label2.setBounds(235, 25, 50, 17);
        label2.setText("密  码：");
        
        passText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
        passText.setBounds(291, 22, 150, 23);
        
        Label label3 = new Label(shell, SWT.NONE);
        label3.setBounds(10, 70, 61, 17);
        label3.setText("验证码：");
        
        final Label imageCodeLabel = new Label(shell, SWT.SHADOW_IN | SWT.CENTER);
        imageCodeLabel.setAlignment(SWT.CENTER);
        imageCodeLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
        imageCodeLabel.setBounds(71, 66, 65, 27);
        imageCodeLabel.setText("无法显示！");
        
        imageCodeText = new Text(shell, SWT.BORDER);
        imageCodeText.setBounds(148, 68, 80, 23);
        
        Button button_1 = new Button(shell, SWT.NONE);
        button_1.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		
        	}
        });
        button_1.setBounds(350, 66, 80, 27);
        button_1.setText("登录");
        
        final StyledText styledText = new StyledText(shell, SWT.BORDER);
        styledText.setEditable(false);
        styledText.setBounds(10, 165, 317, 235);
        
        final Button imageCodeButton = new Button(shell, SWT.NONE);
        imageCodeButton.setEnabled(false);
        imageCodeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Image oldImage = imageCodeLabel.getImage();
                if (oldImage != null) {
                    oldImage.dispose();
                }
                InputStream input = null;
                try {
                    input = piao.getCodeImageInputStream();
                } catch (MiClientException e1) {
                    imageCodeLabel.setText("验证码获取异常！");
                }
                Image image = new Image(Display.getCurrent(), input);
                imageCodeLabel.setImage(image);
            }
        });
        imageCodeButton.setBounds(240, 67, 80, 25);
        imageCodeButton.setText("刷新验证码");
        
        Button button = new Button(shell, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    piao = new Mi();
                } catch (MiClientException e1) {
                    styledText.append("httpclient初始化失败！");
                    if (e1.getCause() instanceof FileNotFoundException) {
                        styledText.append("找不到浏览器证书！");
                    } else if (e1.getCause() instanceof GeneralSecurityException) {
                        styledText.append("浏览器证书读取失败！");
                    } else {
                        styledText.append("SSL Socket失败！");
                    }
                }
                imageCodeButton.setEnabled(true);
            }
        });
        button.setBounds(341, 373, 80, 27);
        button.setText("开始");
        
        
        
        Label label4 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label4.setBounds(0, 93, 441, 56);

    }
    
    private Mi piao;
    private Text userText;
    private Text passText;
    private Text imageCodeText;
}
