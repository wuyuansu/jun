package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bignerdranch.android.criminalintent.util.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    public static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_PHOTO = "DialogPhoto";
    public static final String PHOTO = "photo";

    private static final int REQUES_CODE = 0;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUES_PHONE = 2;
    private static final int REQUES_PHOTO = 3;

    private int mphotoWidth;
    private int mPhotoHeight;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mPhoneButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private LinearLayout mLinearLayout;
    private CallBacks mCallbacks;

    public interface CallBacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (CallBacks) activity;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

//        mLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mphotoWidth = mPhotoView.getWidth();
//                mPhotoHeight = mPhotoView.getHeight();
//
//                mPhotoView.getViewTreeObserver()
//                        .removeOnGlobalLayoutListener(this);
//            }
//        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);



//        mLinearLayout = (LinearLayout) v.findViewById(R.id.crime_photo_camere);

//        mPhotoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                mphotoWidth = mPhotoView.getWidth();
//                mPhotoHeight = mPhotoView.getHeight();
//
//                mPhotoView.getViewTreeObserver().removeOnPreDrawListener(this);
//                return true;
//            }
//        });
//        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mphotoWidth = mPhotoView.getWidth();
//                mPhotoHeight = mPhotoView.getHeight();
//
//                mPhotoView.getViewTreeObserver()
//                        .removeOnGlobalLayoutListener(this);
//            }
//        });

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = DatePickerFragment
//                        .newInstance(mCrime.getDate());
//                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
//                dialog.show(manager, DIALOG_DATE);

                Intent intent = new Intent(getActivity(), DatePickerActivity.class);
                intent.putExtra(DIALOG_DATE, mCrime.getDate());
                startActivityForResult(intent, REQUEST_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .getIntent();

//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT,
//                        getString(R.string.crime_report_subject));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }



        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUES_PHOTO);
//                FragmentManager manager = getFragmentManager();
//                ShowPhotoFragment photoFragment = ShowPhotoFragment.newInstance(mPhotoFile);
//                photoFragment.show(manager, PHOTO);
            }
        });



//        mLinearLayout = (LinearLayout) v.findViewById(R.id.crime_photo_camere);
//        mLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        mphotoWidth = mPhotoView.getWidth();
//                        mPhotoHeight = mPhotoView.getHeight();
//
////                        mLinearLayout.getViewTreeObserver()
////                                .removeOnGlobalLayoutListener(this);
//                    }
//                });



        mPhoneButton = (Button) v.findViewById(R.id.crime_phone);
        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUES_PHONE);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                ShowPhotoFragment photoFragment = ShowPhotoFragment.newInstance(mPhotoFile);
                photoFragment.show(manager, PHOTO);
            }
        });

        return v;
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            
//            mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mphotoWidth = mPhotoView.getWidth();
//                mPhotoHeight = mPhotoView.getHeight();
//
//                mPhotoView.getViewTreeObserver()
//                        .removeOnGlobalLayoutListener(this);
//            }
//        });
//            Bitmap bitmap = PictureUtils
//                    .getScaledBitmap(mPhotoFile.getPath(), mphotoWidth, mPhotoHeight);
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private String getPhoneNumber(String id) {
        String number = null;

//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//        String[] projection = new String[]{
//                ContactsContract.Contacts._ID};
//        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
//        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
//                id = cursor.getInt(0);
//        }

        String[] phoneProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phonesCusor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                null,
                null);
        if (phonesCusor != null && phonesCusor.moveToFirst()) {
             number = phonesCusor.getString(phonesCusor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        return number;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {

                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUES_PHONE && data != null) {
            Uri phoneUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts._ID
            };

            Cursor c = getActivity().getContentResolver()
                    .query(phoneUri, queryFields, null, null, null);

            try {
                if (c.getCount() == 0)
                    return;

                c.moveToFirst();
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String phoneNum = getPhoneNumber(id);
                Uri u = Uri.parse("tel:" + phoneNum);
                Intent intent = new Intent(Intent.ACTION_DIAL, u);
                startActivity(intent);

            } finally {
                c.close();
            }

        } else if (requestCode == REQUES_PHOTO) {
            updatePhotoView();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                CrimeLab.get(getActivity()).delete(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }



    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null)
            suspect = getString(R.string.crime_report_no_suspect);
        else
            suspect = getString(R.string.crime_report_suspect, suspect);

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
