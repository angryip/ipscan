package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.gui.ResultTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import java.util.Collection;

/**
 * A popup menu shown when right-clicking a column header.
 * Lists all available fetchers with check marks for the currently visible ones,
 * allowing the user to toggle column visibility directly.
 */
public class ColumnVisibilityMenu extends ExtendableMenu {
	public ColumnVisibilityMenu(Shell parent, FetcherRegistry fetcherRegistry, ResultTable resultTable) {
		super(parent, SWT.POP_UP);

		// rebuild the items every time the menu is shown, so check marks reflect current visibility
		addListener(SWT.Show, e -> {
			for (var item : getItems()) item.dispose();

			Collection<Fetcher> registered = fetcherRegistry.getRegisteredFetchers();
			Collection<Fetcher> selected = fetcherRegistry.getSelectedFetchers();

			for (var fetcher : registered) {
				// the IP column must always remain visible, so it is not offered as a toggle option
				if (IPFetcher.ID.equals(fetcher.getId())) continue;

				var item = new MenuItem(this, SWT.CHECK);
				item.setText(fetcher.getFullName());
				item.setSelection(selected.contains(fetcher));
				item.setData(fetcher);
				item.addListener(SWT.Selection, ev -> {
					var f = (Fetcher) ev.widget.getData();
					toggleFetcher(fetcherRegistry, resultTable, f);
				});
			}
		});
	}

	private void toggleFetcher(FetcherRegistry fetcherRegistry, ResultTable resultTable, Fetcher fetcher) {
		var selected = fetcherRegistry.getSelectedFetchers();
		var newSelection = new java.util.ArrayList<Fetcher>(selected);
		if (newSelection.contains(fetcher))
			newSelection.remove(fetcher);
		else
			newSelection.add(fetcher);

		var ids = newSelection.stream()
				.map(Fetcher::getId)
				.toArray(String[]::new);
		fetcherRegistry.updateSelectedFetchers(ids);
	}
}
