package com.example.ofoegbuvalentine.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ofoegbuvalentine.popularmovies.R;
import com.example.ofoegbuvalentine.popularmovies.data.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.ofoegbuvalentine.popularmovies.R.layout.movie_review_item;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewsHolder> {

    private Context mContext;
    private ArrayList<Review> mReviewsList;

    public MovieReviewsAdapter(Context context, ArrayList<Review> reviewsList) {
        mContext = context;
        mReviewsList = reviewsList;
    }

    @Override
    public ReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(movie_review_item, parent, false);
        return new ReviewsHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsHolder holder, int position) {
        holder.setAuthorName(mReviewsList.get(position).getAuthor());
        holder.setReview(mReviewsList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviewsList == null) return 0;

        return mReviewsList.size();
    }

    class ReviewsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_author_name)
        TextView mAuthorName;
        @BindView(R.id.tv_review_content)
        TextView mReview;

        public ReviewsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setAuthorName(String authorName) {
            mAuthorName.setText(authorName);
        }

        void setReview(String review) {
            mReview.setText(review);
        }
    }
}

