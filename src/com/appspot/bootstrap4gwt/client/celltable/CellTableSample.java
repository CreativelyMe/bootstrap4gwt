package com.appspot.bootstrap4gwt.client.celltable;

import java.util.ArrayList;
import java.util.List;


import com.appspot.bootstrap4gwt.client.service.HelloWorldService;
import com.appspot.bootstrap4gwt.client.service.HelloWorldServiceAsync;
import com.appspot.bootstrap4gwt.shared.model.Person;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Anchor;

public class CellTableSample extends Composite {
    
    HelloWorldServiceAsync service = GWT.create(HelloWorldService.class);

	private static CellTableSampleUiBinder uiBinder = GWT.create(CellTableSampleUiBinder.class);

	interface CellTableSampleUiBinder extends UiBinder<Widget, CellTableSample> {
	}

	@UiField
	Button button;
	
	@UiField(provided=true)
	CellTable<Person> cellTable = new CellTable<Person>();
	
    SimplePager pager = new SimplePager();
    
    @UiField
    FlowPanel pagingPanel;
	
	List<Person> values = new ArrayList<Person>();
	
	@UiField
	FormPanel form;
	
	@UiField
	TextBox age;

	@UiField
	TextBox name;

	@UiField
	TextBox address;
	
	@UiField
	Button cancel;
	
	@UiField
	ListBox sex;
	@UiField Anchor prev;
	@UiField Anchor next;

	public CellTableSample() {
		initWidget(uiBinder.createAndBindUi(this));
		
		TextColumn<Person> ageClm = new TextColumn<Person>() {
			@Override
			public String getValue(Person object) {
				return object.getAge().toString();
			}
		};
		cellTable.addColumn(ageClm, "Age");

		TextColumn<Person> nameClm = new TextColumn<Person>() {
			@Override
			public String getValue(Person object) {
				return object.getName();
			}
		};
		cellTable.addColumn(nameClm, "Name");

		TextColumn<Person> addressClm = new TextColumn<Person>() {
			@Override
			public String getValue(Person object) {
				return object.getAddress();
			}
		};
		cellTable.addColumn(addressClm, "Address");

        TextColumn<Person> sexClm = new TextColumn<Person>() {
            @Override
            public String getValue(Person object) {
                return object.getSex();
            }
        };
        cellTable.addColumn(sexClm, "Sex");
		
		reloadPersons();
	}
	
	private void reloadPersons() {
        service.getPersons(new AsyncCallback<List<Person>>() {
            @Override
            public void onSuccess(List<Person> result) {
                values.clear();
                values.addAll(result);
                cellTable.setRowCount(values.size(), true);
                cellTable.setRowData(0, values);
                cellTable.redraw();

                pager.setDisplay(cellTable);
                pagingPanel.clear();
                for (int i = 0; i < pager.getPageSize(); i ++) {
                	if (values.size() <= pager.getPageSize() * i) {
                		break;
                	}
                	final int index = i;
                	final PageAnchor pageAnchor = new PageAnchor(String.valueOf(index)) {
        				@Override
        				void onAnchorClick(ClickEvent event) {
        					PageAnchor before = (PageAnchor) pagingPanel.getWidget(pager.getPage());
        					before.deactivation();
        					
        			    	pager.setPage(index);
        			    	activation();
        			        cellTable.setRowCount(values.size(), true);
        			        cellTable.setRowData(0, values);
        			        cellTable.redraw();
        				}
        			};
        			if (index == pager.getPage()) pageAnchor.activation(); 
        			pagingPanel.add(pageAnchor);
                }
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
	}
	
	@UiHandler("age")
	void onKeyPress(KeyPressEvent event) {
		if (event.getCharCode() < '0' || event.getCharCode() > '9') {
			Element elm = age.getElement().getParentElement().getParentElement();
			elm.addClassName("error");
			event.preventDefault();
		}
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		final Person person = new Person(Long.valueOf(age.getValue()), 
		                                 name.getValue(), 
		                                 address.getValue(), 
		                                 sex.getValue(sex.getSelectedIndex()));
		
		service.addPerson(person, new AsyncCallback<Person>() {
            @Override
            public void onSuccess(Person result) {
            	reloadPersons();
                form.reset();
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
	}

    @UiHandler("cancel")
    void onCancelClick(ClickEvent event) {
        form.reset();
        age.setFocus(true);
    }
	
    @UiHandler("prev")
	void onPrevClick(ClickEvent event) {
    	setPageAndRedraw(pager.getPage() - 1);
	}
	
    @UiHandler("next")
	void onNextClick(ClickEvent event) {
    	setPageAndRedraw(pager.getPage() + 1);
	}
    
    void setPageAndRedraw(int page) {
    	if (page < 0 || page > pager.getPageCount()) {
    		return;
    	}
		PageAnchor before = (PageAnchor) pagingPanel.getWidget(pager.getPage());
		before.deactivation();
		
    	pager.setPage(page);
    	
		PageAnchor after = (PageAnchor) pagingPanel.getWidget(pager.getPage());
		after.activation();

        cellTable.setRowCount(values.size(), true);
        cellTable.setRowData(0, values);
        cellTable.redraw();
    }
}
