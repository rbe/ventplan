/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.component.classes;

import com.seebass.tools.Tools;
import com.westaflex.component.classes.XRaum.*;
import java.util.Vector;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author seebass
 */
public class Rooms implements TableModel {

    private int[] count = null;
    private String[] columnIdentifiers;
    private Vector<Raum> dataVector;
    private Vector<TableModelListener> tml = null;
    private RaumDataProviderSingleton dataProvider = RaumDataProviderSingleton.getInstance();

    public Rooms() {

        this.dataVector = new Vector<Raum>();
        count = new int[Geschoss.values().length];

        columnIdentifiers = dataProvider.getColumnNames();
    }

    /**
     *
     * @param geschoss
     * @return Raum
     */
    public Raum add( String geschoss ) {

        Raum r = new Raum();
        int id = createID( geschoss );

        if ( id > 0 ) {
            r.setRaumItemValue( RaumItem.PROP.RAUMNUMMER, id );
            return add( r );
        } else {
            Tools.errbox( "Sie haben das Limit von Räumen pro Geschoss erreicht!" );
        }
        return null;
    }

    public void fillFromTableModel( TableModel model ) {

        for ( int i = 0; i < model.getRowCount(); i++ ) {

            Raum r = new Raum();
            Vector<Tuer> tTest = r.getTueren();
            for ( int j = 0; j < model.getColumnCount(); j++ ) {
                //if (!columnIdentifiers[j].equals("Tueren")) {
                if ( j != dataProvider.getRaumItemByProp( RaumItem.PROP.TUEREN ).getColumnIndex() ) {
                    //r.setValueFromString(columnIdentifiers[j], model.getValueAt(i, j).toString());
                    r.setValueFromString( j, model.getValueAt( i, j ).toString() );
                } else {
                    String strTuer = ( (String) model.getValueAt( i, j ) );
                    if ( !( strTuer == null || strTuer.equals( "" ) ) ) {
                        String[] tuer = ( (String) model.getValueAt( i, j ) ).split( "\\|" );
                        for ( int k = 0; k < tuer.length; k++ ) {
                            Tuer t = new Tuer( tuer[k] );
                            tTest.add( t );
                        }
                        r.setRaumItemValue(
                                RaumDataProviderSingleton.getInstance().getRaumItemByColumnIndex( j ).getProp(),
                                tTest);
                    }
                }
            }
            add( r );
            setMaxID(r);
        }
    }

    /**
     *
     * @param index
     * @return Raum if available\notherwise null
     */
    public Raum get( int index ) {

        return ( index < dataVector.size() ? (Raum) dataVector.elementAt( index ) : null );
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex ) {
        RaumItem raumItem = dataProvider.getRaumItemByColumnIndex( columnIndex );
        StringBuffer ret = new StringBuffer();
        Raum r = (Raum) dataVector.elementAt( rowIndex );
        if ( raumItem.getProp().equals( RaumItem.PROP.TUEREN ) ) {
            Vector<Tuer> t = r.getTueren();
            if ( t != null ) {
                for ( Tuer tuer : t ) {
                    ret.append( tuer.toString() );
                    ret.append( "|" );
                }
            }
            if ( ret.length() > 0 ) {
                ret.deleteCharAt( ret.length() - 1 );
            }
        } else {
            ret.append( r.getRaumItemValue( raumItem.getProp() ) );
        }
        return ret.toString();
    }

    public void removeRow( int row ) {
        dataVector.remove( row );
    }

    @Override
    public void setValueAt( Object val, int row, int col ) {
        RaumItem raumItem = dataProvider.getRaumItemByColumnIndex( col );

        Raum r = (Raum) dataVector.elementAt( row );
        String[] doorsString = null;
        if ( raumItem.getProp().equals( RaumItem.PROP.TUEREN ) ) {
            if ( val instanceof Vector ) {
                r.setRaumItemValue( RaumItem.PROP.TUEREN, val );
            } else {
                doorsString = ( (String) val ).split( "|" );
                Vector<Tuer> doors = new Vector<Tuer>( doorsString.length );
                for ( String string : doorsString ) {
                    doors.add( new Tuer( string ) );
                }
                r.setRaumItemValue( raumItem.getProp(), doors );
            }
        }
        dataVector.setElementAt( r, row );
    }

    /**
     *
     * @param r
     * @return
     */
    public Raum add( Raum r ) {

        return ( dataVector.add( r ) == true ? r : null );
    }

    @Override
    public int getColumnCount() {

        return RaumItem.PROP.values().length;
        // 19.08.2009 return ( dataVector.size() > 0 ? ( (Raum) dataVector.elementAt( 0 ) ).getSize() : 0 );
    }

    /**
     *
     * @param geschoss
     * @return
     */
    private int createID( String geschoss ) {
        int id = 0;
        Geschoss g = Geschoss.valueOf( geschoss );
        if ( count[g.value()] < 100 ) {
            count[g.value()] += 1;
            id = g.value() * 100 + count[g.value()];
        }
        return id;
    }

    private void setMaxID(Raum r){
        String geschossName =  (String) r.getRaumItemValue( RaumItem.PROP.GESCHOSS);
        int g = Geschoss.valueOf( geschossName ).value();
        int i = Integer.parseInt((String)r.getRaumItemValue( RaumItem.PROP.RAUMNUMMER)) - g*100;
        if (count[g] < i){
            count[g]=i;
        }
    }

    @Override
    public int getRowCount() {
        return dataVector.size();
    }

    @Override
    public String getColumnName( int columnIndex ) {
        return columnIdentifiers[columnIndex];
    }

    @Override
    public Class<?> getColumnClass( int columnIndex ) {
        RaumItem raumItem = dataProvider.getRaumItemByColumnIndex( columnIndex );
        return raumItem.getClass();
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return true;
    }

    @Override
    public void addTableModelListener( TableModelListener l ) {
        if ( tml == null ) {
            tml = new Vector<TableModelListener>();
        }
        tml.add( l );
    }

    @Override
    public void removeTableModelListener( TableModelListener l ) {
        if ( tml != null ) {
            tml.remove( l );
        }
    }

    public void moveDown(int rowToMove){
        if( rowToMove == 0 ) return;

        Raum raumToMove = dataVector.get( rowToMove );
        dataVector.insertElementAt( raumToMove, rowToMove - 1 );

        dataVector.removeElementAt( rowToMove + 1 );
    }

    public void moveUp(int rowToMove){
        if( rowToMove == dataVector.size()-1 ) return;
        
        Raum raumToMove = dataVector.get( rowToMove );
        dataVector.insertElementAt( raumToMove, rowToMove + 2 );

        dataVector.removeElementAt( rowToMove );
    }
}
