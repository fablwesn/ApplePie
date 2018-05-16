/*
 * Copyright (C) 2018 Darijo Barucic, Seventoes
 *
 *  Licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fablwesn.education.applepie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fablwesn.education.applepie.data.models.StepsModel;
import com.fablwesn.education.applepie.fragments.DetailsFragment;

import java.util.List;

import static com.fablwesn.education.applepie.fragments.DetailsFragment.KEY_NUMB;
import static com.fablwesn.education.applepie.fragments.DetailsFragment.KEY_STEP;

public class DetailsActivity extends AppCompatActivity {

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */

    private List<StepsModel> stepsList;
    private int stepPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        // Load which step to display and then show it inside fragment
        Intent intent = getIntent();
        stepsList = intent.getParcelableArrayListExtra(KEY_STEP);
        stepPosition = intent.getIntExtra(KEY_NUMB,0);

        if(savedInstanceState == null){
            DetailsFragment detailsFragment = DetailsFragment.newInstance(stepsList, stepPosition);
            getSupportFragmentManager().beginTransaction().add(R.id.details_container, detailsFragment).commit();
        }
    }

    /*____________________________________________________________________________________________*/
}
