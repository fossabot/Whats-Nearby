package com.teester.whatsnearby.data.questions;

import com.teester.whatsnearby.R;

public class DebitCard extends Question {

	public DebitCard() {
		question = R.string.debit_card;
		drawable = R.drawable.ic_credit_card;
		color = R.color.green;
		tag = "payment:debit_cards";
	}

}
