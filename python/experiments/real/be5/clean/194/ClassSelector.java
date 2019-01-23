package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.editors.CustomEditorSupport;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassSelector extends CustomEditorSupport
{
    private final JTextField textField = new JTextField();
    private final JPanel panel;

    private static PlatformClassSelector classSelector;

    public ClassSelector()
    {
        panel = new JPanel(new BorderLayout());
        panel.add(textField);
        JButton editButton = new JButton("...");
        panel.add(editButton, BorderLayout.EAST);

        editButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editButtonAction();
            }
        });
    }

    protected void editButtonAction()
    {
        if (classSelector != null)
        {
            classSelector.selectClass((BeModelElement) getBean(), getDescriptor().getDisplayName(), textField.getText(),
                    new PlatformClassSelectorCallback()
                    {
                        @Override
                        public void classSelected(final String fullyQualifiedName)
                        {
                            EventQueue.invokeLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    textField.setText(fullyQualifiedName);
                                    textField.requestFocus();
                                    textField.selectAll();
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public Object getValue()
    {
        return textField.getText();
    }

    @Override
    public void setValue(Object text)
    {
        textField.setText(text == null ? null : text.toString());
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        textField.addActionListener(this);
        return panel;
    }

    public static void setClassSelector(PlatformClassSelector classSelector)
    {
        ClassSelector.classSelector = classSelector;
    }
}
