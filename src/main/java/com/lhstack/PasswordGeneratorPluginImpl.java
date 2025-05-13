package com.lhstack;

import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.newui.ColorButton;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBSlider;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.lhstack.tools.plugins.Helper;
import com.lhstack.tools.plugins.IPlugin;
import org.apache.commons.lang3.RandomStringUtils;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.prompt.BuddyButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PasswordGeneratorPluginImpl implements IPlugin {

    private final Map<String, JComponent> componentCache = new HashMap<>();

    private final String lowerChars = "abcdefghijklmnopqrstuvwxyz";

    private final String upperChars = lowerChars.toUpperCase();


    @Override
    public Icon pluginIcon() {
        return Helper.findIcon("plugin.svg", PasswordGeneratorPluginImpl.class);
    }

    @Override
    public JComponent createPanel(Project project) {
        return componentCache.computeIfAbsent(project.getLocationHash(), (key) -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JBIntSpinner jbIntSpinner = new JBIntSpinner(16, 0, 100);
            JBSlider jbSlider = new JBSlider(0, 100);

            JCheckBox abc = new JCheckBox("abc    ", true) {
                {
                    this.setPreferredSize(new Dimension(68, 30));
                }
            };

            JCheckBox ABC = new JCheckBox("ABC    ", true) {
                {
                    this.setPreferredSize(new Dimension(68, 30));
                }
            };

            JCheckBox number = new JCheckBox("123    ", true) {
                {
                    this.setPreferredSize(new Dimension(68, 30));
                }
            };

            JCheckBox custom = new JCheckBox("", false) {
                {
                    this.setPreferredSize(new Dimension(27, 30));
                }
            };

            JBTextField customField = new JBTextField("~!@#$%^&*()_+';:><.,/?|\\}{][\"") {
                {
                    this.setMaximumSize(new Dimension(10000, 30));
                }
            };

            JCheckBox excludeRepeat = new JCheckBox("", true);
            //内容
            JTextArea jbTextArea = getJbTextArea();
            JComponent copy = Helper.actionButton(AllIcons.Actions.Copy,"复制密码",32,32,projectLocation -> {
                CopyPasteManager.getInstance().setContents(new StringSelection(jbTextArea.getText()));
                new Notification("", "复制成功,你的密码: " + jbTextArea.getText(), NotificationType.INFORMATION)
                        .setTitle("密码生成")
                        .notify(project);
            });
            copy.setBorder(JBUI.Borders.compound(JBUI.Borders.empty(0,4)));
            JComponent refresh = Helper.actionButton(AllIcons.Actions.Refresh,"刷新",32,32,projectLocation -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
            });
            refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);

            abc.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
            });

            ABC.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
            });

            number.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
            });

            custom.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
            });
            customField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (custom.isSelected()) {
                        refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (custom.isSelected()) {
                        refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (custom.isSelected()) {
                        refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
                    }
                }
            });

            excludeRepeat.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
            });

            //密码长度
            {
                jbIntSpinner.setPreferredSize(new Dimension(65, 30));
                jbSlider.setPreferredSize(new Dimension(100, 30));
                JLabel passwordState = new JLabel("很强", JLabel.LEFT);
                passwordState.setPreferredSize(new Dimension(30, 30));
                JPanel passwordLengthPanel = new JPanel();
                jbSlider.setToolTipText("很强");
                jbSlider.setValue(16);
                jbSlider.addChangeListener(e -> {
                    int value = jbSlider.getValue();
                    jbIntSpinner.setNumber(value);
                    if (value >= 8 && value < 16) {
                        passwordState.setText("一般");
                        jbSlider.setToolTipText("一般");
                    } else if (value < 8) {
                        passwordState.setText("简单");
                        jbSlider.setToolTipText("简单");
                    } else {
                        passwordState.setText("很强");
                        jbSlider.setToolTipText("很强");
                    }
                    refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
                });
                jbIntSpinner.addChangeListener(e -> {
                    int value = jbIntSpinner.getNumber();
                    jbSlider.setValue(value);
                    refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, jbTextArea);
                });
                passwordLengthPanel.setLayout(new BoxLayout(passwordLengthPanel, BoxLayout.X_AXIS));
                passwordLengthPanel.add(new JLabel(" 密码长度:", JLabel.RIGHT));
                passwordLengthPanel.add(jbSlider);
                passwordLengthPanel.add(passwordState);
                passwordLengthPanel.add(jbIntSpinner);
                panel.add(passwordLengthPanel);
            }
            //所含字符
            {
                JPanel containsCharPanel = new JPanel();
                containsCharPanel.setLayout(new BoxLayout(containsCharPanel, BoxLayout.X_AXIS));
                containsCharPanel.add(new JLabel(" 所含字符:  ", JLabel.RIGHT));
                containsCharPanel.add(abc);
                containsCharPanel.add(ABC);
                containsCharPanel.add(number);
                containsCharPanel.add(custom);
                containsCharPanel.add(customField);
                panel.add(containsCharPanel);
            }

            {

                JPanel pane = new JPanel(new BorderLayout());

                JPanel otherPanel = new JPanel(new BorderLayout());
                otherPanel.add(new JLabel(" 排除相似字符:  "), BorderLayout.WEST);
                otherPanel.add(excludeRepeat, BorderLayout.CENTER);
                pane.add(otherPanel, BorderLayout.NORTH);
                pane.add(jbTextArea, BorderLayout.CENTER);
                JPanel copayRefreshPane = new JPanel();
                copayRefreshPane.setLayout(new BorderLayout());
                copayRefreshPane.add(copy, BorderLayout.CENTER);
                copayRefreshPane.add(refresh, BorderLayout.EAST);
                pane.add(copayRefreshPane, BorderLayout.SOUTH);

                panel.add(pane);
            }
            return panel;
        });
    }

    private void refreshPasswd(JBIntSpinner spinner, JCheckBox abc, JCheckBox ABC, JCheckBox number, JCheckBox excludeRepeat, JCheckBox custom, JTextField customField, JTextArea jbTextArea) {
        if (spinner.getNumber() == 0) {
            jbTextArea.setText("");
            return;
        }
        StringBuilder randomChars = new StringBuilder();
        if (abc.isSelected()) {
            randomChars.append(lowerChars);
        }
        if (ABC.isSelected()) {
            randomChars.append(upperChars);
        }
        if (number.isSelected()) {
            String numbers = "0123456789";
            randomChars.append(numbers);
        }
        if (custom.isSelected()) {
            randomChars.append(customField.getText());
        }
        if (excludeRepeat.isSelected()) {
            int length = randomChars.length();
            Random random = new Random();
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < spinner.getNumber(); i++) {
                char nextChar = randomChars.charAt(random.nextInt(length));
                if (i == 1) {
                    int count = 0;
                    while (count <= 50) {
                        nextChar = randomChars.charAt(random.nextInt(length));
                        if (nextChar != output.charAt(0)) {
                            break;
                        }
                        count++;
                    }
                }
                if (i == 2) {
                    int count = 0;
                    while (count <= 50) {
                        nextChar = randomChars.charAt(random.nextInt(length));
                        if (nextChar != output.charAt(0) && nextChar != output.charAt(1)) {
                            break;
                        }
                        count++;
                    }
                }

                if (i == 3) {
                    int count = 0;
                    while (count <= 50) {
                        nextChar = randomChars.charAt(random.nextInt(length));
                        if (nextChar != output.charAt(0) && nextChar != output.charAt(1) && nextChar != output.charAt(2)) {
                            break;
                        }
                        count++;
                    }
                }

                if (i >= 4) {
                    int count = 0;
                    while (count <= 50) {
                        nextChar = randomChars.charAt(random.nextInt(length));
                        if (nextChar != output.charAt(0) && nextChar != output.charAt(1) && nextChar != output.charAt(2) && nextChar != output.charAt(3)) {
                            break;
                        }
                        count++;
                    }
                }
                output.append(nextChar);
            }
            jbTextArea.setText(output.toString());
        } else {
            jbTextArea.setText(RandomStringUtils.random(spinner.getNumber(), randomChars.toString()));
        }

    }

    private static @NotNull JTextArea getJbTextArea() {
        JBTextArea jbTextArea = new JBTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                // 绘制背景
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                // 绘制居中文本
                paintCenteredText((Graphics2D) g.create());
            }

            private void paintCenteredText(Graphics2D g2d) {
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                if (text.isEmpty()) return;

                // 获取边框的内边距
                Insets insets = getInsets();
                int availableWidth = getWidth() - insets.left - insets.right;
                int availableHeight = getHeight() - insets.top - insets.bottom;

                String[] lines = text.split("\n");
                int totalHeight = fm.getHeight() * lines.length;
                // 计算垂直居中位置（考虑内边距）
                int y = insets.top + (availableHeight - totalHeight) / 2 + fm.getAscent();

                g2d.setColor(getForeground());
                for (String line : lines) {
                    int lineWidth = fm.stringWidth(line);
                    // 计算水平居中位置（考虑内边距）
                    int x = insets.left + (availableWidth - lineWidth) / 2;
                    g2d.drawString(line, x, y);
                    y += fm.getHeight();
                }
                g2d.dispose();
            }
        };
        jbTextArea.setFont(new Font("Monospaced", Font.PLAIN, 24));
        jbTextArea.setWrapStyleWord(true);
        jbTextArea.setLineWrap(true);
        jbTextArea.setBorder(JBUI.Borders.compound(JBUI.Borders.empty(0, 4), JBUI.Borders.customLine(JBColor.GRAY)));
        jbTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        jbTextArea.setAlignmentY(Component.CENTER_ALIGNMENT);
        jbTextArea.setEditable(false);
        return jbTextArea;
    }

    @Override
    public Icon pluginTabIcon() {
        return Helper.findIcon("plugin-tab.svg", PasswordGeneratorPluginImpl.class);
    }

    @Override
    public String pluginName() {
        return "密码生成";
    }

    @Override
    public String pluginDesc() {
        return "生成任意密码";
    }

    @Override
    public String pluginVersion() {
        return "0.0.1";
    }
}
