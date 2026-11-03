package ec.edu.monster.phoneshop.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ec.edu.monster.phoneshop.R;
import ec.edu.monster.phoneshop.dto.MovementDto;
import lombok.Getter;

public class MovementsListAdapter extends RecyclerView.Adapter<MovementsListAdapter.ViewHolder> {
    private final List<MovementDto> movements;

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView referenceTextView;
        private final TextView amountTextView;
        private final TextView dateTextView;
        private final TextView typeTextView;
        private final TextView targetAccountTextView;

        public ViewHolder(View view) {
            super(view);

            referenceTextView = view.findViewById(R.id.referenceTextView);
            amountTextView = view.findViewById(R.id.amountTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            typeTextView = view.findViewById(R.id.movementTypeTextView);
            targetAccountTextView = view.findViewById(R.id.targetAccountTextView);
        }
    }

    public MovementsListAdapter(List<MovementDto> movements) {
        this.movements = movements;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movements_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        MovementDto movement = movements.get(position);

        viewHolder.getReferenceTextView().setText(movement.getReference());
        viewHolder.getAmountTextView().setText(String.format("%.2f", movement.getAmount(), movement.getAmount().doubleValue()));
        viewHolder.getDateTextView().setText(movement.getCreatedAt());
        viewHolder.getTypeTextView().setText(movement.getType().getLabel());
        viewHolder.getTargetAccountTextView().setText(movement.getTargetAccount().getReference());
    }

    @Override
    public int getItemCount() {
        return movements.size();
    }

    public void setMovements(List<MovementDto> movements) {
        this.movements.clear();
        this.movements.addAll(movements);
        notifyDataSetChanged();
    }
}
