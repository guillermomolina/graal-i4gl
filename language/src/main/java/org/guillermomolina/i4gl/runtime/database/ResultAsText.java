package org.guillermomolina.i4gl.runtime.database;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

//drop table t;
//
//create table t (
//  a int,
//  b decimal(10, 2),
//  c varchar(10),
//  d char(6),
//  employment_date date,
//  f timestamp,
//  g time
//);
//
//insert into t (a, b, c, d, employment_date, f, g) values (123, 456.10, 'Chicago', 'Four', curdate(), current_timestamp(), curtime());
//
//insert into t (a, b, c, d, employment_date, f, g) values (-108, 1234567.13, 'Detroit', 'Eleven', curdate(), current_timestamp(), curtime());
//
//select * from t;

public class ResultAsText {
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultAsText.class);
    private static final int MAX_CELL_WIDTH = 1000000;

    private ColumnDisplayDefinition[] _colDefs;
    private int _rowCount = 0;

    private boolean _showRowNumbers;

    public ResultAsText(ColumnDisplayDefinition[] colDefs, boolean showHeadings) {
        this(colDefs, showHeadings, true);
    }

    public ResultAsText(ColumnDisplayDefinition[] colDefs, boolean showHeadings, boolean showRowNumbers) {
        _colDefs = colDefs;
        _rowCount = 0;
        _showRowNumbers = showRowNumbers;

        if (showHeadings) {
            if (_showRowNumbers) {
                System.out.print(" |");
            }

            for (int i = 0; i < _colDefs.length; ++i) {
                System.out.print(colDefs[i].getColumnHeading());
                System.out.print("|");
            }
            System.out.print("\n");
        }
    }

    public void addRow(Object[] row) {
        _rowCount++;

        if (_showRowNumbers) {
            System.out.print(Integer.toString(_rowCount));
            System.out.print("|");
        }

        for (int i = 0; i < _colDefs.length; ++i) {
            String cellValue = CellComponentFactory.renderObject(row[i], this._colDefs[i]);
            System.out.print(cellValue);
            System.out.print("|");
        }
        System.out.print("\n");
    }

    public int getRowCount() {
        return _rowCount;
    }

}
