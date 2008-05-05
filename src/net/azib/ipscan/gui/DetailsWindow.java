/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.CommentsConfig;
import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.fetchers.CommentFetcher;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The "Show IP Details" Window
 *
 * @author Anton Keks
 */
public class DetailsWindow extends AbstractModalDialog {

	private GUIConfig guiConfig;
	private CommentsConfig commentsConfig;
	private ResultTable resultTable;
	
	private Text commentsText;
		
	public DetailsWindow(GUIConfig guiConfig, CommentsConfig commentsConfig, ResultTable resultTable) {
		this.guiConfig = guiConfig;
		this.commentsConfig = commentsConfig;
		this.resultTable = resultTable;
	}
	
	@Override
	public void open() {
		createShell(resultTable.getShell());
		super.open();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell(Shell parent) {
		shell = new Shell(parent, SWT.TOOL | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setText(Labels.getLabel("title.details"));
		shell.setImage(parent.getImage());
		shell.setLayout(LayoutHelper.formLayout(3, 3, 3));
		shell.setSize(guiConfig.detailsWindowSize);
		
		ScanningResult result = resultTable.getSelectedResult();
		
		commentsText = new Text(shell, SWT.BORDER);
		commentsText.pack();
		commentsText.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), null, new FormAttachment(100)));
		CommentsTextListener commentsTextListener = new CommentsTextListener();
		commentsText.addFocusListener(commentsTextListener);
		commentsText.addModifyListener(commentsTextListener);
		String comment = commentsConfig.getComment(result.getAddress());
		if (comment != null) {
			commentsText.setText(comment);
		}
		else {
			commentsTextListener.focusLost(null);
		}

		Text detailsText = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		detailsText.setText(result.toString());
		detailsText.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		detailsText.setTabs(32);
		detailsText.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(0), new FormAttachment(commentsText)));
						
		Listener traverseListener = new TraverseListener();
		detailsText.addListener(SWT.Traverse, traverseListener);
		commentsText.addListener(SWT.Traverse, traverseListener);
		
		shell.layout();
		detailsText.forceFocus();
	}
	
	class CommentsTextListener implements FocusListener, ModifyListener {
		String defaultText = Labels.getLabel("text.comment.edit");
		
		public void focusGained(FocusEvent e) {
			if (commentsText.getText().equals(defaultText)) {
				commentsText.setText("");
				commentsText.setForeground(commentsText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
			}
		}

		public void focusLost(FocusEvent e) {
			if (commentsText.getText().isEmpty()) {
				commentsText.setText(defaultText);
				commentsText.setForeground(commentsText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			}
		}

		public void modifyText(ModifyEvent e) {
			String newComment = commentsText.getText();
			if (!defaultText.equals(newComment)) {
				// store the new comment
				commentsConfig.setComment(resultTable.getSelectedResult().getAddress(), newComment);
				// now update the result table for user to immediately see the change
				resultTable.updateResult(resultTable.getSelectionIndex(), CommentFetcher.ID, newComment);
			}
		}
	}
	
	class TraverseListener implements Listener {
		public void handleEvent(Event e) {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				guiConfig.detailsWindowSize = shell.getSize();				
				shell.close();
			}
		}
	}
}
