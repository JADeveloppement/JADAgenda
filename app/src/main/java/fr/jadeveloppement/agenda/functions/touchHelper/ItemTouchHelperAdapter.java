package fr.jadeveloppement.agenda.functions.touchHelper;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismissInterface();

    void onItemDropped();
}
