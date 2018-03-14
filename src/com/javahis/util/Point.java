package com.javahis.util;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
class Point
{
        public int mx;
        public int my;

        public Point(int inX,int inY)
        {
                mx=inX;
                my=inY;
        }

        public String toString()
        {
                return new String(mx+","+my);
        }
}
