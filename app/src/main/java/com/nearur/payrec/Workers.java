package com.nearur.payrec;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Workers extends Fragment {

    private SectionPagerAdapter sectionPagerAdapter;
    public Boss boss;
    private AddEmployee employee;

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public Workers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_workers, container, false);

        final ViewPager viewPager=(ViewPager)v.findViewById(R.id.viewpg);
        sectionPagerAdapter=new SectionPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);


        TabLayout tabLayout=(TabLayout)v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(2).setIcon(R.drawable.arrow);

        employee=new AddEmployee();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    if(position == 2){
                        employee.setBoss(boss);
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        return v;
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter{

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Salary();
                case 1:
                    return new Piecerate();
                case 2:
                    return employee;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Salaried";
                case 1:
                    return "Piece Rate";

            }
            return null;
        }
    }



}
