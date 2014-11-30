/*
* Copyright © 2014 George T. Steel
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.oshkimaadziig.george.androidutils;

import android.annotation.TargetApi;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;


/**
 * Associated Action bar tabs with fragments. Create one of these for each tab and
 * pass it the class and arguments for that tab's fragment.
 * The tabs choose a fragment to show in specified layout.
 * Unlike the example given in the android docs,
 * this implementation uses tags let the fragment manager recreate fragments from their icicles
 * when activities are recreated (after an OOM kill or configuration change)
 * instead of instantiating fresh fragments each time.
 * 
 * @author George T. Steel
 * 
 * @see {@link http://developer.android.com/guide/topics/ui/actionbar.html#Tabs}
 */
public class FragmentTabListener implements ActionBar.TabListener {
	private final int _layoutSlot;
	private Fragment _fragment;
    private final String _tag;
    private final Class<? extends Fragment> _fragClass;
    private final Bundle _args;


    /**
     * Create a new listener for a particular Tab-Fragment pair.
     * 
     * @param activity The host Activity, used to get the fragment manager.
     * @param layoutSlot The id for layout containing the fragments.
     * @param fragmentClass The type of fragment to create for this tab.
     * @param tag The unique identifier tag for the fragment (must not be null).
     * @param args Arguments to initialize the fragment with (may be null).
     */
	public FragmentTabListener(Activity activity, int layoutSlot, Class<? extends Fragment> fragmentClass, String tag, Bundle args) {
		_tag = tag;
		_layoutSlot = layoutSlot;
		_fragment = activity.getFragmentManager().findFragmentByTag(tag);
		_args = (args == null? new Bundle(): args);
		_fragClass = fragmentClass;
	}
	
	/**
	 * Shorter version of the constructor, with some defaults set.
	 * Args are set to null and layoutSlot is set to {@link android.R.id.content}
	 * (the single layout in the activity before {@link Activity.setContentView} is called).
	 * 
	 * @param activity The id for layout containing the fragments.
	 * @param fragmentClass The type of fragment to create for this tab.
	 * @param tag The unique identifier tag for the fragment (must not be null).
	 */
	public FragmentTabListener(Activity activity, Class<? extends Fragment> fragmentClass, String tag) {
		this(activity, android.R.id.content, fragmentClass, tag, null);
	}

	/**
	 * Unused. 
	 * {@inheritDoc}
	 */
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	/**
	 * Re-attaches the fragment being switched to.
	 * If the fragment has not being created yet, creates a new fragment of the correct type (with arguments specified in the constructor)
	 * and adds it to the specified layout.
	 * {@inheritDoc}
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (_fragment == null) {
            try {
				_fragment = _fragClass.newInstance();
				_fragment.setArguments(_args);
				ft.add(_layoutSlot, _fragment, _tag);
			} catch (InstantiationException e) {}
              catch (IllegalAccessException e) {}
              // Fragment classes have blank public default constructors: these are never thrown
        } else {
            ft.attach(_fragment);
        }
	}

	/**
	 * Detaches the fragment being switched away from.
	 * {@inheritDoc}
	 */
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (_fragment != null) {
            ft.detach(_fragment);
        }
	}
}
