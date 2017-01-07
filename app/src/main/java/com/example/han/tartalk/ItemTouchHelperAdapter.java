package com.example.han.tartalk;

/**
 * Created by han on 03/01/2017.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
