package org.opencv.core;

import java.nio.ByteBuffer;

// C++: class Mat
//javadoc: Mat
public class Mat {

    public final long nativeObj;

    public Mat(long addr) {
        if (addr == 0)
            throw new UnsupportedOperationException("Native object address is NULL");
        nativeObj = addr;
    }

    //
    // C++: Mat::Mat()
    //

    // javadoc: Mat::Mat()
    public Mat() {
        nativeObj = n_Mat();
    }

    //
    // C++: Mat::Mat(Mat m, Range rowRange, Range colRange = Range::all())
    //

    // javadoc: Mat::Mat(m, rowRange, colRange)
    public Mat(Mat m, Range rowRange, Range colRange) {
        nativeObj = n_Mat(m.nativeObj, rowRange.start, rowRange.end, colRange.start, colRange.end);
    }

    // javadoc: Mat::Mat(m, rowRange)
    public Mat(Mat m, Range rowRange) {
        nativeObj = n_Mat(m.nativeObj, rowRange.start, rowRange.end);
    }

    // javadoc: Mat::checkVector(elemChannels, depth)
    public int checkVector(int elemChannels, int depth) {
        return n_checkVector(nativeObj, elemChannels, depth);
    }

    //
    // C++: Mat Mat::clone()
    //

    // javadoc: Mat::clone()
    public Mat clone() {
        return new Mat(n_clone(nativeObj));
    }


    //
    // C++: int Mat::dims()
    //

    // javadoc: Mat::dims()
    public int dims() {
        return n_dims(nativeObj);
    }


    // javadoc: Mat::create(rows, cols, type)
    public void create(int rows, int cols, int type) {
        n_create(nativeObj, rows, cols, type);
    }


    // javadoc: Mat::dataAddr()
    public long dataAddr() {
        return n_dataAddr(nativeObj);
    }


    //
    // C++: bool Mat::empty()
    //

    // javadoc: Mat::empty()
    public boolean empty() {
        return n_empty(nativeObj);
    }

    // javadoc: Mat::isContinuous()
    public boolean isContinuous() {
        return n_isContinuous(nativeObj);
    }

    //
    // C++: bool Mat::isSubmatrix()
    //

    // javadoc: Mat::isSubmatrix()
    public boolean isSubmatrix() {
        return n_isSubmatrix(nativeObj);
    }


    // javadoc: Mat::size(int i)
    public int size(int i) {
        return n_size_i(nativeObj, i);
    }

    // javadoc: Mat::total()
    public long total() {
        return n_total(nativeObj);
    }

    //
    // C++: int Mat::type()
    //

    // javadoc: Mat::type()
    public int type() {
        return n_type(nativeObj);
    }


    @Override
    protected void finalize() throws Throwable {
        n_delete(nativeObj);
        super.finalize();
    }

    // javadoc:Mat::toString()
    @Override
    public String toString() {
        String _dims = (dims() > 0) ? "" : "-1*-1*";
        for (int i=0; i<dims(); i++) {
            _dims += size(i) + "*";
        }
        return "Mat [ " + _dims + CvType.typeToString(type()) +
                ", isCont=" + isContinuous() + ", isSubmat=" + isSubmatrix() +
                ", nativeObj=0x" + Long.toHexString(nativeObj) +
                ", dataAddr=0x" + Long.toHexString(dataAddr()) +
                " ]";
    }

    // javadoc:Mat::dump()
    public String dump() {
        return nDump(nativeObj);
    }

    // javadoc:Mat::put(row,col,data)
    public int put(int row, int col, int[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0)
            throw new UnsupportedOperationException(
                    "Provided data element number (" +
                            (data == null ? 0 : data.length) +
                            ") should be multiple of the Mat channels count (" +
                            CvType.channels(t) + ")");
        if (CvType.depth(t) == CvType.CV_32S) {
            return nPutI(nativeObj, row, col, data.length, data);
        }
        throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
    }


    // javadoc:Mat::get(row,col,data)
    public int get(int row, int col, int[] data) {
        int t = type();
        if (data == null || data.length % CvType.channels(t) != 0)
            throw new UnsupportedOperationException(
                    "Provided data element number (" +
                            (data == null ? 0 : data.length) +
                            ") should be multiple of the Mat channels count (" +
                            CvType.channels(t) + ")");
        if (CvType.depth(t) == CvType.CV_32S) {
            return nGetI(nativeObj, row, col, data.length, data);
        }
        throw new UnsupportedOperationException("Mat data type is not compatible: " + t);
    }

    // javadoc:Mat::getNativeObjAddr()
    public long getNativeObjAddr() {
        return nativeObj;
    }

    // C++: Mat::Mat()
    private static native long n_Mat();

    // C++: Mat::Mat(int rows, int cols, int type)
    private static native long n_Mat(int rows, int cols, int type);

    // C++: Mat::Mat(int ndims, const int* sizes, int type)
    private static native long n_Mat(int ndims, int[] sizes, int type);

    // C++: Mat::Mat(int rows, int cols, int type, void* data)
    private static native long n_Mat(int rows, int cols, int type, ByteBuffer data);

    // C++: Mat::Mat(Size size, int type)
    private static native long n_Mat(double size_width, double size_height, int type);

    // C++: Mat::Mat(int rows, int cols, int type, Scalar s)
    private static native long n_Mat(int rows, int cols, int type, double s_val0, double s_val1, double s_val2, double s_val3);

    // C++: Mat::Mat(Size size, int type, Scalar s)
    private static native long n_Mat(double size_width, double size_height, int type, double s_val0, double s_val1, double s_val2, double s_val3);

    // C++: Mat::Mat(int ndims, const int* sizes, int type, Scalar s)
    private static native long n_Mat(int ndims, int[] sizes, int type, double s_val0, double s_val1, double s_val2, double s_val3);

    // C++: Mat::Mat(Mat m, Range rowRange, Range colRange = Range::all())
    private static native long n_Mat(long m_nativeObj, int rowRange_start, int rowRange_end, int colRange_start, int colRange_end);

    private static native long n_Mat(long m_nativeObj, int rowRange_start, int rowRange_end);

    // C++: Mat::Mat(const Mat& m, const std::vector<Range>& ranges)
    private static native long n_Mat(long m_nativeObj, Range[] ranges);

    // C++: Mat Mat::adjustROI(int dtop, int dbottom, int dleft, int dright)
    private static native long n_adjustROI(long nativeObj, int dtop, int dbottom, int dleft, int dright);

    // C++: void Mat::assignTo(Mat m, int type = -1)
    private static native void n_assignTo(long nativeObj, long m_nativeObj, int type);

    private static native void n_assignTo(long nativeObj, long m_nativeObj);

    // C++: int Mat::channels()
    private static native int n_channels(long nativeObj);

    // C++: int Mat::checkVector(int elemChannels, int depth = -1, bool
    // requireContinuous = true)
    private static native int n_checkVector(long nativeObj, int elemChannels, int depth, boolean requireContinuous);

    private static native int n_checkVector(long nativeObj, int elemChannels, int depth);

    private static native int n_checkVector(long nativeObj, int elemChannels);

    // C++: Mat Mat::clone()
    private static native long n_clone(long nativeObj);

    // C++: Mat Mat::col(int x)
    private static native long n_col(long nativeObj, int x);

    // C++: Mat Mat::colRange(int startcol, int endcol)
    private static native long n_colRange(long nativeObj, int startcol, int endcol);

    // C++: int Mat::dims()
    private static native int n_dims(long nativeObj);

    // C++: int Mat::cols()
    private static native int n_cols(long nativeObj);

    // C++: void Mat::convertTo(Mat& m, int rtype, double alpha = 1, double beta
    // = 0)
    private static native void n_convertTo(long nativeObj, long m_nativeObj, int rtype, double alpha, double beta);

    private static native void n_convertTo(long nativeObj, long m_nativeObj, int rtype, double alpha);

    private static native void n_convertTo(long nativeObj, long m_nativeObj, int rtype);

    // C++: void Mat::copyTo(Mat& m)
    private static native void n_copyTo(long nativeObj, long m_nativeObj);

    // C++: void Mat::copyTo(Mat& m, Mat mask)
    private static native void n_copyTo(long nativeObj, long m_nativeObj, long mask_nativeObj);

    // C++: void Mat::create(int rows, int cols, int type)
    private static native void n_create(long nativeObj, int rows, int cols, int type);

    // C++: void Mat::create(Size size, int type)
    private static native void n_create(long nativeObj, double size_width, double size_height, int type);

    // C++: void Mat::create(int ndims, const int* sizes, int type)
    private static native void n_create(long nativeObj, int ndims, int[] sizes, int type);

    // C++: void Mat::copySize(const Mat& m)
    private static native void n_copySize(long nativeObj, long m_nativeObj);

    // C++: Mat Mat::cross(Mat m)
    private static native long n_cross(long nativeObj, long m_nativeObj);

    // C++: long Mat::dataAddr()
    private static native long n_dataAddr(long nativeObj);

    // C++: int Mat::depth()
    private static native int n_depth(long nativeObj);

    // C++: Mat Mat::diag(int d = 0)
    private static native long n_diag(long nativeObj, int d);

    // C++: static Mat Mat::diag(Mat d)
    private static native long n_diag(long d_nativeObj);

    // C++: double Mat::dot(Mat m)
    private static native double n_dot(long nativeObj, long m_nativeObj);

    // C++: size_t Mat::elemSize()
    private static native long n_elemSize(long nativeObj);

    // C++: size_t Mat::elemSize1()
    private static native long n_elemSize1(long nativeObj);

    // C++: bool Mat::empty()
    private static native boolean n_empty(long nativeObj);

    // C++: static Mat Mat::eye(int rows, int cols, int type)
    private static native long n_eye(int rows, int cols, int type);

    // C++: static Mat Mat::eye(Size size, int type)
    private static native long n_eye(double size_width, double size_height, int type);

    // C++: Mat Mat::inv(int method = DECOMP_LU)
    private static native long n_inv(long nativeObj, int method);

    private static native long n_inv(long nativeObj);

    // C++: bool Mat::isContinuous()
    private static native boolean n_isContinuous(long nativeObj);

    // C++: bool Mat::isSubmatrix()
    private static native boolean n_isSubmatrix(long nativeObj);

    // C++: void Mat::locateROI(Size wholeSize, Point ofs)
    private static native void locateROI_0(long nativeObj, double[] wholeSize_out, double[] ofs_out);

    // C++: Mat Mat::mul(Mat m, double scale = 1)
    private static native long n_mul(long nativeObj, long m_nativeObj, double scale);

    private static native long n_mul(long nativeObj, long m_nativeObj);

    // C++: static Mat Mat::ones(int rows, int cols, int type)
    private static native long n_ones(int rows, int cols, int type);

    // C++: static Mat Mat::ones(Size size, int type)
    private static native long n_ones(double size_width, double size_height, int type);

    // C++: static Mat Mat::ones(int ndims, const int* sizes, int type)
    private static native long n_ones(int ndims, int[] sizes, int type);

    // C++: void Mat::push_back(Mat m)
    private static native void n_push_back(long nativeObj, long m_nativeObj);

    // C++: void Mat::release()
    private static native void n_release(long nativeObj);

    // C++: Mat Mat::reshape(int cn, int rows = 0)
    private static native long n_reshape(long nativeObj, int cn, int rows);

    private static native long n_reshape(long nativeObj, int cn);

    // C++: Mat Mat::reshape(int cn, int newndims, const int* newsz)
    private static native long n_reshape_1(long nativeObj, int cn, int newndims, int[] newsz);

    // C++: Mat Mat::row(int y)
    private static native long n_row(long nativeObj, int y);

    // C++: Mat Mat::rowRange(int startrow, int endrow)
    private static native long n_rowRange(long nativeObj, int startrow, int endrow);

    // C++: int Mat::rows()
    private static native int n_rows(long nativeObj);

    // C++: Mat Mat::operator =(Scalar s)
    private static native long n_setTo(long nativeObj, double s_val0, double s_val1, double s_val2, double s_val3);

    // C++: Mat Mat::setTo(Scalar value, Mat mask = Mat())
    private static native long n_setTo(long nativeObj, double s_val0, double s_val1, double s_val2, double s_val3, long mask_nativeObj);

    // C++: Mat Mat::setTo(Mat value, Mat mask = Mat())
    private static native long n_setTo(long nativeObj, long value_nativeObj, long mask_nativeObj);

    private static native long n_setTo(long nativeObj, long value_nativeObj);

    // C++: Size Mat::size()
    private static native double[] n_size(long nativeObj);

    // C++: int Mat::size(int i)
    private static native int n_size_i(long nativeObj, int i);

    // C++: size_t Mat::step1(int i = 0)
    private static native long n_step1(long nativeObj, int i);

    private static native long n_step1(long nativeObj);

    // C++: Mat Mat::operator()(Range rowRange, Range colRange)
    private static native long n_submat_rr(long nativeObj, int rowRange_start, int rowRange_end, int colRange_start, int colRange_end);

    // C++: Mat Mat::operator()(const std::vector<Range>& ranges)
    private static native long n_submat_ranges(long nativeObj, Range[] ranges);

    // C++: Mat Mat::operator()(Rect roi)
    private static native long n_submat(long nativeObj, int roi_x, int roi_y, int roi_width, int roi_height);

    // C++: Mat Mat::t()
    private static native long n_t(long nativeObj);

    // C++: size_t Mat::total()
    private static native long n_total(long nativeObj);

    // C++: int Mat::type()
    private static native int n_type(long nativeObj);

    // C++: static Mat Mat::zeros(int rows, int cols, int type)
    private static native long n_zeros(int rows, int cols, int type);

    // C++: static Mat Mat::zeros(Size size, int type)
    private static native long n_zeros(double size_width, double size_height, int type);

    // C++: static Mat Mat::zeros(int ndims, const int* sizes, int type)
    private static native long n_zeros(int ndims, int[] sizes, int type);

    // native support for java finalize()
    private static native void n_delete(long nativeObj);

    private static native int nPutD(long self, int row, int col, int count, double[] data);

    private static native int nPutDIdx(long self, int[] idx, int count, double[] data);

    private static native int nPutF(long self, int row, int col, int count, float[] data);

    private static native int nPutFIdx(long self, int[] idx, int count, float[] data);

    private static native int nPutI(long self, int row, int col, int count, int[] data);

    private static native int nPutIIdx(long self, int[] idx, int count, int[] data);

    private static native int nPutS(long self, int row, int col, int count, short[] data);

    private static native int nPutSIdx(long self, int[] idx, int count, short[] data);

    private static native int nPutB(long self, int row, int col, int count, byte[] data);

    private static native int nPutBIdx(long self, int[] idx, int count, byte[] data);

    private static native int nPutBwOffset(long self, int row, int col, int count, int offset, byte[] data);

    private static native int nPutBwIdxOffset(long self, int[] idx, int count, int offset, byte[] data);

    private static native int nGetB(long self, int row, int col, int count, byte[] vals);

    private static native int nGetBIdx(long self, int[] idx, int count, byte[] vals);

    private static native int nGetS(long self, int row, int col, int count, short[] vals);

    private static native int nGetSIdx(long self, int[] idx, int count, short[] vals);

    private static native int nGetI(long self, int row, int col, int count, int[] vals);

    private static native int nGetIIdx(long self, int[] idx, int count, int[] vals);

    private static native int nGetF(long self, int row, int col, int count, float[] vals);

    private static native int nGetFIdx(long self, int[] idx, int count, float[] vals);

    private static native int nGetD(long self, int row, int col, int count, double[] vals);

    private static native int nGetDIdx(long self, int[] idx, int count, double[] vals);

    private static native double[] nGet(long self, int row, int col);

    private static native double[] nGetIdx(long self, int[] idx);

    private static native String nDump(long self);
}
