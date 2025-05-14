package com.lhstack;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBSlider;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.lhstack.tools.plugins.Helper;
import com.lhstack.tools.plugins.IPlugin;
import org.apache.commons.lang3.RandomStringUtils;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

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

            JBTextField customField = new JBTextField("~!@#$%^&*()_+';:.,/?|}{][\"") {
                {
                    this.setMaximumSize(new Dimension(10000, 30));
                }
            };

            JCheckBox excludeRepeat = new JCheckBox("", true);
            JBIntSpinner passwordNums = new JBIntSpinner(1,0,16){
                {
                    this.setPreferredSize(new Dimension(68, 30));
                }
            };

            //内容
            JBLabel contentLabel = new JBLabel("", JBLabel.CENTER);
            contentLabel.setVerticalAlignment(JBLabel.CENTER);
            contentLabel.setHorizontalAlignment(JBLabel.CENTER);
//            contentLabel.setBorder(JBUI.Borders.compound(JBUI.Borders.empty(0, 4), JBUI.Borders.customLine(JBColor.GRAY)));
            contentLabel.setFont(new Font("Monospaced", Font.PLAIN, 24));
            contentLabel.setCopyable(true);
            JComponent copy = Helper.actionButton(AllIcons.Actions.Copy, "复制密码", 32, 32, projectLocation -> {
                CopyPasteManager.getInstance().setContents(new StringSelection(contentLabel.getText()));
                new Notification("", "复制成功,你的密码: " + contentLabel.getText(), NotificationType.INFORMATION)
                        .setTitle("密码生成")
                        .notify(project);
            });
            copy.setBorder(JBUI.Borders.compound(JBUI.Borders.empty(0, 4)));
            JComponent refresh = Helper.actionButton(AllIcons.Actions.Refresh, "刷新", 32, 32, projectLocation -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
            });
            refresh.setBorder(JBUI.Borders.emptyRight(4));
            refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);

            passwordNums.addChangeListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
            });
            abc.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
            });

            ABC.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
            });

            number.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
            });

            custom.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
            });
            customField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (custom.isSelected()) {
                        refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (custom.isSelected()) {
                        refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (custom.isSelected()) {
                        refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
                    }
                }
            });

            excludeRepeat.addActionListener(e -> {
                refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
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
                    refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
                });
                jbIntSpinner.addChangeListener(e -> {
                    int value = jbIntSpinner.getNumber();
                    jbSlider.setValue(value);
                    refreshPasswd(jbIntSpinner, abc, ABC, number, excludeRepeat, custom, customField, contentLabel,passwordNums);
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

                JPanel otherPanel = new JPanel();
                {
                    otherPanel.setLayout(new HorizontalLayout());
                    JPanel excludePanel = new JPanel(new BorderLayout());
                    excludePanel.add(new JLabel(" 排除相似字符:  "), BorderLayout.WEST);
                    excludePanel.add(excludeRepeat, BorderLayout.CENTER);

                    JPanel passwordNumberPanel = new JPanel(new BorderLayout());
                    passwordNumberPanel.add(new JLabel("  数量:  "), BorderLayout.WEST);
                    passwordNumberPanel.add(passwordNums, BorderLayout.CENTER);
                    otherPanel.add(excludePanel);
                    otherPanel.add(passwordNumberPanel);
                }
                pane.add(otherPanel, BorderLayout.NORTH);
                pane.add(new JBScrollPane(contentLabel), BorderLayout.CENTER);
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

    private void refreshPasswd(JBIntSpinner spinner, JCheckBox abc, JCheckBox ABC, JCheckBox number, JCheckBox excludeRepeat, JCheckBox custom, JTextField customField, JLabel contentLabel,JBIntSpinner passwordNum) {
        if (spinner.getNumber() == 0) {
            contentLabel.setText("");
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
            Supplier<String> passwordGen = () -> {
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
                return output.toString();
            };
            StringBuilder numbers = new StringBuilder();
            for (int i = 0; i < passwordNum.getNumber(); i++) {
                numbers.append("<div style='text-align: center'>%s</div>".formatted(passwordGen.get()));
            }
            contentLabel.setText(numbers.toString());
        } else {
            StringBuilder numbers = new StringBuilder();
            for (int i = 0; i < passwordNum.getNumber(); i++) {
                numbers.append("<div style='text-align: center'>%s</div>".formatted(RandomStringUtils.random(spinner.getNumber(), randomChars.toString())));
            }
            contentLabel.setText(numbers.toString());
        }

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
