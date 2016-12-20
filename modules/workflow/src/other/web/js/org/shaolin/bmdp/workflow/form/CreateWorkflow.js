/* null */
/* auto generated constructor */
function org_shaolin_bmdp_workflow_form_CreateWorkflow(json)
{
    var prefix = (typeof(json) == "string") ? json : json.prefix; 
    var idUI = new UIMaster.ui.hidden
    ({
        ui: elementList[prefix + "idUI"]
    });

    var workflowNameUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "workflowNameUILabel"]
    });

    var workflowNameUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "workflowNameUI"]
    });

    var descriptionUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "descriptionUILabel"]
    });

    var descriptionUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "descriptionUI"]
    });

    var flowNameUILabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "flowNameUILabel"]
    });

    var flowNameUI = new UIMaster.ui.textfield
    ({
        ui: elementList[prefix + "flowNameUI"]
    });

    var variableLabel = new UIMaster.ui.label
    ({
        ui: elementList[prefix + "variableLabel"]
    });

    var okbtn = new UIMaster.ui.button
    ({
        ui: elementList[prefix + "okbtn"]
    });

    var cancelbtn = new UIMaster.ui.button
    ({
        ui: elementList[prefix + "cancelbtn"]
    });

    var variableTable = new org_shaolin_bmdp_workflow_form_VariableDefinitionTable({"prefix":prefix + "variableTable."});

    var actionPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "actionPanel"]
        ,items: []
        ,subComponents: [prefix + "okbtn",prefix + "cancelbtn"]
    });

    var fieldPanel = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "fieldPanel"]
        ,items: []
        ,subComponents: [prefix + "idUI",prefix + "workflowNameUILabel",prefix + "workflowNameUI",prefix + "descriptionUILabel",prefix + "descriptionUI",prefix + "flowNameUILabel",prefix + "flowNameUI",prefix + "variableLabel",prefix + "variableTable"]
    });

    var Form = new UIMaster.ui.panel
    ({
        ui: elementList[prefix + "Form"]
        ,items: [idUI,workflowNameUILabel,workflowNameUI,descriptionUILabel,descriptionUI,flowNameUILabel,flowNameUI,variableLabel,okbtn,cancelbtn,variableTable,fieldPanel,actionPanel]
    });

    Form.idUI=idUI;

    Form.workflowNameUILabel=workflowNameUILabel;

    Form.workflowNameUI=workflowNameUI;

    Form.descriptionUILabel=descriptionUILabel;

    Form.descriptionUI=descriptionUI;

    Form.flowNameUILabel=flowNameUILabel;

    Form.flowNameUI=flowNameUI;

    Form.variableLabel=variableLabel;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.variableTable=variableTable;

    Form.fieldPanel=fieldPanel;

    Form.idUI=idUI;

    Form.workflowNameUILabel=workflowNameUILabel;

    Form.workflowNameUI=workflowNameUI;

    Form.descriptionUILabel=descriptionUILabel;

    Form.descriptionUI=descriptionUI;

    Form.flowNameUILabel=flowNameUILabel;

    Form.flowNameUI=flowNameUI;

    Form.variableLabel=variableLabel;

    Form.variableTable=variableTable;

    Form.actionPanel=actionPanel;

    Form.okbtn=okbtn;

    Form.cancelbtn=cancelbtn;

    Form.user_constructor = function()
    {
        /* Construct_FIRST:org_shaolin_bmdp_workflow_form_CreateWorkflow */
        /* Construct_LAST:org_shaolin_bmdp_workflow_form_CreateWorkflow */
    };

    Form.Save = org_shaolin_bmdp_workflow_form_CreateWorkflow_Save;

    Form.Cancel = org_shaolin_bmdp_workflow_form_CreateWorkflow_Cancel;

    Form.invokeDynamicFunction = org_shaolin_bmdp_workflow_form_CreateWorkflow_invokeDynamicFunction;

    Form.__entityName="org.shaolin.bmdp.workflow.form.CreateWorkflow";

    Form.init();
    return Form;
};

    /* EventHandler Functions */
/* Other_Func_FIRST:org_shaolin_bmdp_workflow_form_CreateWorkflow */
/* Other_Func_LAST:org_shaolin_bmdp_workflow_form_CreateWorkflow */

    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateWorkflow_Save(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateWorkflow_Save */
        var o = this;
        var UIEntity = this;

        {   
            var constraint_result = this.Form.validate();
            if (constraint_result != true && constraint_result != null) {
                return false;
            }
        }
        {this.variableTable.itemTable.syncBodyDataToServer();}
        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"saveDetail-20150807-230248",UIMaster.getValue(eventsource),o.__entityName);
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateWorkflow_Save */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateWorkflow_Cancel(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateWorkflow_Cancel */
        var o = this;
        var UIEntity = this;

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),"cancelDetail-20150807-230248",UIMaster.getValue(eventsource),o.__entityName);
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateWorkflow_Cancel */


    /* auto generated eventlistener function declaration */
    function org_shaolin_bmdp_workflow_form_CreateWorkflow_invokeDynamicFunction(eventsource,event) {/* Gen_First:org_shaolin_bmdp_workflow_form_CreateWorkflow_invokeDynamicFunction */
        var o = this;
        var UIEntity = this;

        var constraint_result = this.Form.validate();
        if (constraint_result != true && constraint_result != null) {
            return false;
        }

        new UIMaster.ui.dialog({dialogType: UIMaster.ui.dialog.CONFIRM_DIALOG,message:WORKFLOW_COMFORMATION_MSG,messageType:UIMaster.ui.dialog.Warning,optionType:UIMaster.ui.dialog.YES_NO_OPTION,title:'',height:150,width:300,handler: function() {

        // cal ajax function. 

        UIMaster.triggerServerEvent(UIMaster.getUIID(eventsource),event,UIMaster.getValue(eventsource),o.__entityName);

        
        }
        }).open();
    }/* Gen_Last:org_shaolin_bmdp_workflow_form_CreateWorkflow_invokeDynamicFunction */



