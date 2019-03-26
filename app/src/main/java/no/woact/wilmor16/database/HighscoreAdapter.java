package no.woact.wilmor16.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import no.woact.wilmor16.R;

/**
 * Created by Morten
 */

public class HighscoreAdapter extends ArrayAdapter<Highscore> {

    private static class ViewHolder {
        TextView mHighscoreName;
        TextView mHighscoreScore;
    }

    public HighscoreAdapter(Context context, List<Highscore> items) {
        // Resource ID not needed
        super(context,0, items);
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {

        Highscore currentHighscores = getItem(position);

        // Inflate only once
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.adapter_highscore, null, false);
        }

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mHighscoreName = convertView.findViewById(R.id.highscore_name);
        viewHolder.mHighscoreScore = convertView.findViewById(R.id.highscore_score);

        // Store results of findViewById
        convertView.setTag(viewHolder);

        TextView name = ((ViewHolder)convertView.getTag()).mHighscoreName;
        TextView score = ((ViewHolder)convertView.getTag()).mHighscoreScore;

        assert currentHighscores != null;
        name.setText(currentHighscores.getName());
        score.setText(String.valueOf(currentHighscores.getScore()));

        return convertView;
    }
}