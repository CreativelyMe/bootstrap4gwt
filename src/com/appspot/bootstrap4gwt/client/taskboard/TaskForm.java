package com.appspot.bootstrap4gwt.client.taskboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class TaskForm extends Composite {

    private static TaskFormUiBinder uiBinder = GWT
        .create(TaskFormUiBinder.class);

    @UiField
    TextArea subject;

    @UiField
    Button button;

    DialogBox form;

    TaskBoard caller;

    VerticalPanel todo;

    interface TaskFormUiBinder extends UiBinder<Widget, TaskForm> {
    }

    public TaskForm(DialogBox form, VerticalPanel todo, TaskBoard taskBoard) {
        this.form = form;
        this.todo = todo;
        this.caller = taskBoard;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        if (subject.getText().isEmpty()) {
            event.preventDefault();
            return;
        }
        caller.addTask(subject.getText(), todo);
        form.hide();
    }
}
